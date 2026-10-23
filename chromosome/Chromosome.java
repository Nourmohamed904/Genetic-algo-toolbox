package chromosome;

public abstract class Chromosome
{
    public abstract Chromosome copy();

    public abstract int length();

    public abstract String get_Representation_Type();
}