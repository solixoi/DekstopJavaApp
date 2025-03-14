package by.server.utility;

import by.server.models.DTO.*;
import by.server.models.entities.*;
import by.server.models.enums.RequestType;
import by.server.models.enums.ResponseStatus;
import by.server.models.enums.Roles;
import by.server.models.tcp.Request;
import by.server.models.tcp.Response;
import by.server.service.*;
import com.google.gson.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ClientThread implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private final UserService userService = new UserService();
    private final ProductService productService = new ProductService();
    private final RealizationExpensesService realizationExpensesService =  new RealizationExpensesService();
    private final ProductionExpensesService productionExpensesService = new ProductionExpensesService();
    private final LogService logService = new LogService();
    private final PriceHistoryService priceHistoryService = new PriceHistoryService();
    private final ReportService reportService = new ReportService();
    private final BanService banService = new BanService();

    private Request request;
    private Response response;
    private Gson gson;

    public ClientThread(){

    }

    public ClientThread(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        request = new Request();
        response = new Response();
        gson = new GsonBuilder().create();;
    }

    @Override
    public void run() {
        try {
            while (clientSocket.isConnected()) {
                String message = in.readLine();

                request = gson.fromJson(message, Request.class);

                switch (request.getRequestType()){
                    case REGISTER -> {
                        try {
                            User user = gson.fromJson(request.getRequestMessage(), User.class);
                            user.setRole(new Role(user, Roles.USER));
                            user.setBan(new Ban(user));
                            user.setPassword(PasswordHashingUtil.hashPassword(user.getPassword()));
                            userService.save(user);
                            logService.save(user, RequestType.REGISTER.toString());
                            response = new Response(ResponseStatus.OK, "Register", "");;
                        } catch (Exception e) {
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), "");
                        }
                        break;
                    }
                    case LOGIN -> {
                        try {
                            User user = gson.fromJson(request.getRequestMessage(), User.class);
                            user = userService.findByUsernameOrEmailOrPassword(user.getUsername() == null ? user.getEmail() : user.getUsername(), user.getPassword());
                            if(user.getBan().isBanned()){
                                throw new RuntimeException("User banned!");
                            }
                            logService.save(user, RequestType.LOGIN.toString());
                            response = new Response(ResponseStatus.OK, "Login successfully!", gson.toJson(new UserDTO(user)));
                        } catch (Exception e) {
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case CALCULATE_PRODUCT_PRICE -> {
                        try {
                            JsonObject jsonObject = JsonParser.parseString(request.getRequestMessage()).getAsJsonObject();
                            JsonElement productElement = jsonObject.get("product");
                            Product product = gson.fromJson(productElement, Product.class);
                            System.out.println(product);
                            User user = userService.findById(product.getCreatedBy().getUserId());
                            product.setCreatedBy(user);
                            JsonElement realizationExpensesJSON = jsonObject.get("RealizationExpenses");
                            RealizationExpenses realizationExpenses = gson.fromJson(realizationExpensesJSON, RealizationExpenses.class);
                            realizationExpenses.setProduct(product);
                            JsonElement productionExpensesJSON = jsonObject.get("ProductionExpenses");
                            ProductionExpenses productionExpenses = gson.fromJson(productionExpensesJSON, ProductionExpenses.class);
                            productionExpenses.setProduct(product);
                            product.setRealizationExpenses(realizationExpenses);
                            product.setProductionExpenses(productionExpenses);
                            productService.save(product);
                            product = productService.calculateTotalPrice(product.getProductId());
                            JsonObject returnObject = new JsonObject();
                            returnObject.add("product", gson.toJsonTree(new ProductDTO(product)));
                            returnObject.add("RealizationExpenses", gson.toJsonTree(new RealizationExpensesDTO(realizationExpenses)));
                            returnObject.add("ProductionExpenses", gson.toJsonTree(new ProductionExpensesDTO(productionExpenses)));
                            logService.save(user, RequestType.CALCULATE_PRODUCT_PRICE.toString());
                            response = new Response(ResponseStatus.OK, "Calculate successfully!", gson.toJson(returnObject));
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case UPDATE_USER_PRODUCT -> {
                        try {
                            JsonObject jsonObject = JsonParser.parseString(request.getRequestMessage()).getAsJsonObject();
                            JsonElement productElement = jsonObject.get("product");
                            PriceHistory priceHistory = new PriceHistory();
                            Product product = gson.fromJson(productElement, Product.class);
                            priceHistory.setProduct(product);
                            priceHistory.setOldPrice(productService.findById(product.getProductId()).getFinalPrice());
                            ProductionExpenses productionExpenses = null;
                            RealizationExpenses realizationExpenses = null;
                            JsonElement userJSON = jsonObject.get("user");
                            User user = gson.fromJson(userJSON, User.class);
                            user.setRole(userService.findById(user.getUserId()).getRole());
                            product.setCreatedBy(user);
                            JsonElement realizationExpensesJSON = jsonObject.get("RealizationExpenses");
                            if(!realizationExpensesJSON.isJsonNull()){
                                realizationExpenses = gson.fromJson(realizationExpensesJSON, RealizationExpenses.class);
                                System.out.println(product.getProductId());
                                realizationExpenses.setRealizationId(realizationExpensesService.findProductId(product.getProductId()));
                                realizationExpenses.setProduct(product);
                                realizationExpensesService.update(realizationExpenses);
                                product.setCostPrice(realizationExpenses.getDistributionCost().add(realizationExpenses.getMarketingCost()).add(realizationExpenses.getOtherExpenses().add(
                                        realizationExpenses.getTransportationCost())));
                            }
                            JsonElement productionExpensesJSON = jsonObject.get("ProductionExpenses");
                            if(!productionExpensesJSON.isJsonNull()){
                                productionExpenses = gson.fromJson(productionExpensesJSON, ProductionExpenses.class);
                                productionExpenses.setProductionId(productionExpensesService.findProductId(product.getProductId()));
                                productionExpenses.setProduct(product);
                                productionExpensesService.update(productionExpenses);
                                product.setCostPrice(product.getCostPrice().add(productionExpenses.getOtherExpenses()).add(productionExpenses.getWagesCost()).add(productionExpenses.getOverheadCost()).add(
                                        productionExpenses.getMaterialCost()));
                            }
                            productService.update(product);
                            product = productService.calculateTotalPrice(product.getProductId());
                            priceHistory.setNewPrice(product.getFinalPrice());
                            priceHistoryService.save(priceHistory);
                            JsonObject returnObject = new JsonObject();
                            returnObject.add("product", gson.toJsonTree(new ProductDTO(product)));
                            returnObject.add("RealizationExpenses", gson.toJsonTree(new RealizationExpensesDTO(realizationExpenses)));
                            returnObject.add("ProductionExpenses", gson.toJsonTree(new ProductionExpensesDTO(productionExpenses)));
                            logService.save(user, RequestType.UPDATE_USER_PRODUCT.toString());
                            response = new Response(ResponseStatus.OK, "Update successfully!", gson.toJson(returnObject));
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case DELETE_USER_PRODUCT -> {
                        try {
                            Product product = gson.fromJson(request.getRequestMessage(), Product.class);
                            Long usId = product.getCreatedBy().getUserId();
                            product.setCreatedBy(null);
                            productService.update(product);
                            productService.delete(productService.findById(product.getProductId()));
                            User user = userService.findById(usId);
                            List<ProductDTO> products = null;
                            if (!user.getProducts().isEmpty()) {
                                products = new ArrayList<>();
                                for (Product ProductElement : user.getProducts()) {
                                    products.add(new ProductDTO(ProductElement));
                                }
                            }
                            logService.save(user, RequestType.DELETE_USER_PRODUCT.toString());
                            response = new Response(ResponseStatus.OK, "Delete successfully!", gson.toJson(products));
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case GET_USER_PRODUCTS -> {
                        try {
                            User user = gson.fromJson(request.getRequestMessage(), User.class);
                            user = userService.findById(user.getUserId());
                            List<ProductDTO> products = null;
                            if(!user.getProducts().isEmpty()) {
                                products = new ArrayList<>();
                                for (Product product : user.getProducts()) {
                                    products.add(new ProductDTO(product));
                                }
                            }
                            logService.save(user, RequestType.GET_USER_PRODUCTS.toString());
                            response = new Response(ResponseStatus.OK, "Get successfully!", gson.toJson(products));
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case GET_INFO -> {
                        try {
                            System.out.println("Received Request: " + request);
                            Product product = gson.fromJson(request.getRequestMessage(), Product.class);
                            Product returnProduct = productService.findById(product.getProductId());
                            JsonObject returnObject = new JsonObject();
                            returnObject.add("product", gson.toJsonTree(new ProductDTO(returnProduct)));
                            returnObject.add("RealizationExpenses", gson.toJsonTree(new RealizationExpensesDTO(returnProduct.getRealizationExpenses())));
                            returnObject.add("ProductionExpenses", gson.toJsonTree(new ProductionExpensesDTO(returnProduct.getProductionExpenses())));
                            logService.save(product.getCreatedBy(), RequestType.GET_INFO.toString());
                            response = new Response(ResponseStatus.OK, "Get successfully!", gson.toJson(returnObject));
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case UPDATE_USER_ACCOUNT -> {
                        try {
                            User user = gson.fromJson(request.getRequestMessage(), User.class);
                            user.setPassword(PasswordHashingUtil.hashPassword(user.getPassword()));
                            user.setRole(userService.findById(user.getUserId()).getRole());
                            userService.update(user);
                            logService.save(user, RequestType.UPDATE_USER_ACCOUNT.toString());
                            response = new Response(ResponseStatus.OK, "Update successfully!", gson.toJson(new UserDTO(user)));
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case DELETE_USER_ACCOUNT -> {
                        try {
                            User user = gson.fromJson(request.getRequestMessage(), User.class);
                            userService.delete(userService.findById(user.getUserId()));
                            response = new Response(ResponseStatus.OK, "Delete successfully!", null);
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case CHANGE_ROLE -> {
                        try {
                            JsonObject jsonObject = JsonParser.parseString(request.getRequestMessage()).getAsJsonObject();
                            JsonElement userJSON = jsonObject.get("user");
                            JsonElement userID = jsonObject.get("user_id");
                            JsonElement roleJSON = jsonObject.get("role");
                            User user = userService.findById(gson.fromJson(userID, Long.class));
                            if(user.getRole().getRole() == Roles.ADMIN) {
                                throw new RuntimeException("You cant change Admin Role");
                            }
                            user.getRole().setRole(gson.fromJson(roleJSON, Roles.class));
                            userService.update(user);
                            logService.save(gson.fromJson(userJSON, User.class), RequestType.CHANGE_ROLE + " for " + user.getUsername());
                            response = new Response(ResponseStatus.OK, "Change successfully!", gson.toJson(new UserDTO(user)));
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case CHANGE_PRODUCT_DATA -> {
                        try {
                            JsonObject jsonObject = JsonParser.parseString(request.getRequestMessage()).getAsJsonObject();
                            JsonElement productElement = jsonObject.get("product");
                            JsonElement userJSON = jsonObject.get("user");
                            User userAdmin = gson.fromJson(userJSON, User.class);
                            PriceHistory priceHistory = new PriceHistory();
                            Product product = gson.fromJson(productElement, Product.class);
                            priceHistory.setProduct(productService.findById(product.getProductId()));
                            priceHistory.setOldPrice(product.getFinalPrice());
                            ProductionExpenses productionExpenses = null;
                            RealizationExpenses realizationExpenses = null;
                            User user = userService.findById(product.getCreatedBy().getUserId());
                            product.setCreatedBy(user);
                            productService.update(product);
                            JsonElement realizationExpensesJSON = jsonObject.get("RealizationExpenses");
                            if(!realizationExpensesJSON.isJsonNull()){
                                realizationExpenses = gson.fromJson(realizationExpensesJSON, RealizationExpenses.class);
                                realizationExpenses.setProduct(productService.findById(realizationExpenses.getProduct().getProductId()));
                                realizationExpensesService.update(realizationExpenses);
                            }
                            JsonElement productionExpensesJSON = jsonObject.get("ProductionExpenses");
                            if(!productionExpensesJSON.isJsonNull()){
                                productionExpenses = gson.fromJson(productionExpensesJSON, ProductionExpenses.class);
                                productionExpenses.setProduct(productService.findById(productionExpenses.getProduct().getProductId()));
                                productionExpensesService.update(productionExpenses);
                            }
                            product = productService.calculateTotalPrice(product.getProductId());
                            priceHistory.setNewPrice(product.getFinalPrice());
                            priceHistoryService.save(priceHistory);
                            JsonObject returnObject = new JsonObject();
                            returnObject.add("product", gson.toJsonTree(product));
                            returnObject.add("RealizationExpenses", gson.toJsonTree(realizationExpenses));
                            returnObject.add("ProductionExpenses", gson.toJsonTree(productionExpenses));
                            logService.save(userAdmin, RequestType.UPDATE_USER_PRODUCT.toString());
                            response = new Response(ResponseStatus.OK, "Update successfully!", gson.toJson(returnObject));
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case DELETE_PRODUCT -> {
                        try {
                            JsonObject jsonObject = JsonParser.parseString(request.getRequestMessage()).getAsJsonObject();
                            JsonElement productElement = jsonObject.get("product");
                            JsonElement userJSON = jsonObject.get("user");
                            User userAdmin = gson.fromJson(userJSON, User.class);
                            Product product = gson.fromJson(productElement, Product.class);
                            product.setCreatedBy(null);
                            productService.update(product);
                            productService.delete(productService.findById(product.getProductId()));
                            List<ProductDTO> products = null;
                            if(!productService.findAll().isEmpty()) {
                                products = new ArrayList<>();
                                for (Product elementProduct : productService.findAll()) {
                                    products.add(new ProductDTO(elementProduct));
                                }
                            }
                            logService.save(userAdmin, RequestType.DELETE_PRODUCT.toString());
                            response = new Response(ResponseStatus.OK, "Delete successfully!", gson.toJson(products));
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case BAN_USER -> {
                        try {
                            JsonObject jsonObject = JsonParser.parseString(request.getRequestMessage()).getAsJsonObject();
                            JsonElement userJSON = jsonObject.get("user");
                            JsonElement userID = jsonObject.get("user_id");
                            User userAdmin = gson.fromJson(userJSON, User.class);
                            User user = userService.findById(userID.getAsLong());

                            if(user.getRole().getRole() == Roles.ADMIN){
                                throw new RuntimeException("You cant ban admin!");
                            }
                            if (user.getBan().isBanned()){
                                throw new RuntimeException("User is banned!");
                            }

                            user.getBan().setBanned(true);
                            userService.update(user);
                            logService.save(userAdmin, RequestType.BAN_USER + " for " + user.getUsername());
                            response = new Response(ResponseStatus.OK, "Ban user successfully!", gson.toJson(new UserDTO(user)));
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case UNBAN_USER -> {
                        try {
                            JsonObject jsonObject = JsonParser.parseString(request.getRequestMessage()).getAsJsonObject();
                            JsonElement userJSON = jsonObject.get("user");
                            JsonElement userID = jsonObject.get("user_id");
                            User userAdmin = gson.fromJson(userJSON, User.class);
                            User user = userService.findById(userID.getAsLong());
                            if(user.getRole().getRole() == Roles.ADMIN){
                                throw new RuntimeException("This is admin!");
                            }
                            if (!user.getBan().isBanned()){
                                throw new RuntimeException("User is not banned!");
                            }
                            user.getBan().setBanned(false);
                            userService.update(user);
                            logService.save(userAdmin, RequestType.UNBAN_USER + " for " + user.getUsername());
                            response = new Response(ResponseStatus.OK, "Un ban user successfully!", gson.toJson(new UserDTO(user)));
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case DELETE_ACCOUNT -> {
                        try {
                            JsonObject jsonObject = JsonParser.parseString(request.getRequestMessage()).getAsJsonObject();
                            JsonElement userJSON = jsonObject.get("user");
                            JsonElement userID = jsonObject.get("user_id");
                            User userAdmin = gson.fromJson(userJSON, User.class);
                            User user = userService.findById(userID.getAsLong());
                            if(user.getRole().getRole() == Roles.ADMIN){
                                throw new RuntimeException("You cant delete admin account!");
                            }
                            userService.delete(user);
                            List<UserDTO> users = null;
                            if (!userService.findAll().isEmpty()) {
                                users = new ArrayList<>();
                                for (User userElement : userService.findAll()) {
                                    users.add(new UserDTO(userElement));
                                }
                            }
                            System.out.println(users == null);
                            logService.save(userAdmin, RequestType.DELETE_ACCOUNT + " for " + user.getUsername());
                            response = new Response(ResponseStatus.OK, "Delete successfully!", gson.toJson(users));
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case GET_USER_INFO -> {
                        try {
                            JsonObject jsonObject = JsonParser.parseString(request.getRequestMessage()).getAsJsonObject();
                            JsonElement userJSON = jsonObject.get("user");
                            JsonElement userID = jsonObject.get("user_id");
                            User user = userService.findById(userID.getAsLong());
                            User userAdmin = gson.fromJson(userJSON, User.class);
                            logService.save(userAdmin, RequestType.GET_USER_INFO + " for " + user.getUsername());
                            response = new Response(ResponseStatus.OK, "Get user info successfully!", gson.toJson(new UserDTO(user)));
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case GET_LOG_INFO -> {
                        try {
                            JsonObject jsonObject = JsonParser.parseString(request.getRequestMessage()).getAsJsonObject();
                            JsonElement userJSON = jsonObject.get("user");
                            User userAdmin = gson.fromJson(userJSON, User.class);
                            JsonElement userID = jsonObject.get("user_id");

                            byte[] pdfBytes = reportService.createLogReport(gson.fromJson(userID, Long.class));
                            logService.save(userAdmin, RequestType.GET_LOG_INFO.toString());
                            response = new Response(ResponseStatus.OK, "Get successfully!", null, pdfBytes);
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case CREATE_REPORT_MARGINALITY -> {
                        try {
                            JsonObject jsonObject = JsonParser.parseString(request.getRequestMessage()).getAsJsonObject();
                            JsonElement userManagerJSON = jsonObject.get("user");
                            User userManager = gson.fromJson(userManagerJSON, User.class);
                            userManager = userService.findById(userManager.getUserId());
                            JsonElement productIdElement = jsonObject.get("product_id");
                            byte[] pdfBytes = reportService.createReportMarginality(productIdElement.getAsLong());
                            logService.save(userManager, RequestType.CREATE_REPORT_MARGINALITY.toString());
                            response = new Response(ResponseStatus.OK, "Marginality report generated successfully!", null, pdfBytes);
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case CREATE_REPORT_EXPENSES_PRODUCT -> {
                        try {
                            JsonObject jsonObject = JsonParser.parseString(request.getRequestMessage()).getAsJsonObject();
                            JsonElement userManagerJSON = jsonObject.get("user");
                            User userManager = gson.fromJson(userManagerJSON, User.class);
                            userManager = userService.findById(userManager.getUserId());
                            JsonElement productIdElement = jsonObject.get("product_id");
                            byte[] pdfBytes = reportService.createReportExpensesProduct(productIdElement.getAsLong());
                            logService.save(userManager, RequestType.CREATE_REPORT_EXPENSES_PRODUCT.toString());
                            response = new Response(ResponseStatus.OK, "Expenses report generated successfully!", null, pdfBytes);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case GET_PRICE_HISTORY -> {
                        try {
                            JsonObject jsonObject = JsonParser.parseString(request.getRequestMessage()).getAsJsonObject();
                            JsonElement userJSON = jsonObject.get("user");
                            JsonElement productId = jsonObject.get("product_id");
                            Product product = productService.findById(productId.getAsLong());
                            User user = gson.fromJson(userJSON, User.class);
                            List<PriceHistoryDTO> priceHistories = null;
                            if(!product.getPriceHistory().isEmpty()){
                                for(PriceHistory priceHistory : product.getPriceHistory()){
                                    priceHistories = new ArrayList<>();
                                    priceHistories.add(new PriceHistoryDTO(priceHistory));
                                }
                            }
                            logService.save(user, RequestType.GET_PRICE_HISTORY.toString());
                            response = new Response(ResponseStatus.OK, "Get user info successfully!", gson.toJson(priceHistories));
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case GET_ALL_ACCOUNTS -> {
                        try {
                            User user = gson.fromJson(request.getRequestMessage(), User.class);
                            List<UserDTO> users = null;
                            if (!userService.findAll().isEmpty()) {
                                users = new ArrayList<>();
                                for (User userElement : userService.findAll()) {
                                    users.add(new UserDTO(userElement));
                                }
                            }
                            logService.save(user, RequestType.GET_ALL_ACCOUNTS.toString());
                            response = new Response(ResponseStatus.OK, "Get user info successfully!", gson.toJson(users));
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case GET_ALL_PRODUCTS -> {
                        try {
                            User user = gson.fromJson(request.getRequestMessage(), User.class);
                            List<ProductDTO> products = null;
                            if (!productService.findAll().isEmpty()){
                                products = new ArrayList<>();
                                for (Product product : productService.findAll()) {
                                    products.add(new ProductDTO(product));
                                }
                            }
                            logService.save(user, RequestType.GET_ALL_PRODUCTS.toString());
                            response = new Response(ResponseStatus.OK, "Get products info successfully!", gson.toJson(products));
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case CREATE_REPORT_PRICE_HISTORY -> {
                        try {
                            JsonObject jsonObject = JsonParser.parseString(request.getRequestMessage()).getAsJsonObject();
                            JsonElement userManagerJSON = jsonObject.get("user");
                            User userManager = gson.fromJson(userManagerJSON, User.class);
                            userManager = userService.findById(userManager.getUserId());
                            JsonElement productIdElement = jsonObject.get("product_id");
                            byte[] pdfBytes = reportService.createReportHistoryProduct(productIdElement.getAsLong());
                            logService.save(userManager, RequestType.CREATE_REPORT_EXPENSES_PRODUCT.toString());
                            response = new Response(ResponseStatus.OK, "Expenses report generated successfully!", null, pdfBytes);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case GET_LOAD_DATA_MAIN_PAGE -> {
                        try {
                            User user = gson.fromJson(request.getRequestMessage(), User.class);
                            user = userService.findById(user.getUserId());
                            JsonObject returnObject = new JsonObject();
                            long totalChange = 0;
                            for (Product product : user.getProducts()) {
                                for (PriceHistory priceHistory : product.getPriceHistory()) {
                                    totalChange += 1;
                                }
                            }
                            returnObject.add("countEditProductData", gson.toJsonTree(totalChange));
                            returnObject.add("countProducts", gson.toJsonTree(user.getProducts().size()));
                            logService.save(user, RequestType.GET_LOAD_DATA_MAIN_PAGE.toString());
                            response = new Response(ResponseStatus.OK, "Login successfully!", gson.toJson(returnObject));
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                }
                out.println(gson.toJson(response));
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Клиент: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " закрыл соединение.");
            try {
                clientSocket.close();
                in.close();
                out.close();
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
    }
}
