(Very) WIP spell finder/manager for Pathfinder 2e.

Desktop and web versions:

* Web uses React for frontend, Ktor + Postgres w/ Exposed for backend.
* Desktop uses JetBrains Compose for UI, H2 w/ Exposed for DB

Database access, buisness logic, and data structures is shared using Kotlin multiplatform and multi-module projects.

Web is currently non-functional due to non-updated compiler plugins.

Desktop works if you have the DB. It will eventually update itself from the web version's hosted DB, but atm you have to build it yourself using
[Load.kt](common/src/jvmMain/kotlin/com/rnett/spellbook/load/Load.kt).