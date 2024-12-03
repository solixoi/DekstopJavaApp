CREATE OR REPLACE FUNCTION update_cost_price()
RETURNS TRIGGER AS $$
BEGIN
UPDATE public.products
SET cost_price = (
    COALESCE((
                 SELECT SUM(
                                COALESCE(material_cost, 0) +
                                COALESCE(other_expenses, 0) +
                                COALESCE(overhead_cost, 0) +
                                COALESCE(wages_cost, 0)
                        )
                 FROM public.production_expenses
                 WHERE product_id = NEW.product_id
             ), 0)
        +
    COALESCE((
                 SELECT SUM(
                                COALESCE(distribution_cost, 0) +
                                COALESCE(marketing_cost, 0) +
                                COALESCE(other_expenses, 0) +
                                COALESCE(transportation_cost, 0)
                        )
                 FROM public.realization_expenses
                 WHERE product_id = NEW.product_id
             ), 0)
    )
WHERE product_id = NEW.product_id;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_trigger
        WHERE tgname = 'trigger_update_cost_price_production'
    ) THEN
CREATE TRIGGER trigger_update_cost_price_production
AFTER INSERT OR UPDATE ON public.production_expenses
FOR EACH ROW
EXECUTE FUNCTION update_cost_price();
END IF;
    IF NOT EXISTS (
        SELECT 1
        FROM pg_trigger
        WHERE tgname = 'trigger_update_cost_price_realization'
    ) THEN
CREATE TRIGGER trigger_update_cost_price_realization
AFTER INSERT OR UPDATE ON public.realization_expenses
FOR EACH ROW
EXECUTE FUNCTION update_cost_price();
END IF;
END $$;