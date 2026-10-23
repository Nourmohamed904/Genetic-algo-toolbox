package problem.cvrp;

public class Customer {
    private final int id;
    private final double x;
    private final double y;
    private final double demand;

    public Customer(int id, double x, double y, double demand) {
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException(
                "Customer coordinates must be finite."
            );
        }

        if (!Double.isFinite(demand) || demand < 0) {
            throw new IllegalArgumentException(
                "Customer demand must be finite and non-negative."
            );
        }

        this.id = id;
        this.x = x;
        this.y = y;
        this.demand = demand;
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getDemand() {
        return demand;
    }
}