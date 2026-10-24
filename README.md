# RouteOpt-GA

**Genetic Algorithm Toolbox for Capacitated Vehicle Routing**

RouteOpt-GA is a Java project for optimizing vehicle routes using genetic
algorithms. It targets the **Capacitated Vehicle Routing Problem (CVRP)**:
visit every customer, start and finish each route at a depot, and minimize total
travel distance without exceeding vehicle capacities or the available fleet size.

The reusable toolbox separates chromosome representations and genetic operators
from problem-specific fitness evaluation and feasibility checks. It includes
binary, integer, and floating-point representations, selection and crossover
components, and mutation strategies implemented from scratch, without GA
libraries or Google OR-Tools.

**Status:** toolbox components and CVRP modeling are available; the complete
CVRP optimization workflow is still under development.

## Run

Requires **JDK 21 or newer**, with `java` and `javac` on PATH. In PowerShell:

```powershell
.\scripts\test.ps1
.\scripts\run.ps1
```

The application lists its available commands. Production code is in
`src/main/java`, tests are in `src/test/java`, and generated build output goes
into the Git-ignored `out/` folder.
