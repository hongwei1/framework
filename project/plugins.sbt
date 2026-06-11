DefaultOptions.addPluginResolvers
resolvers += Resolver.typesafeRepo("releases")

// OBP fork: GitHub disabled the git:// protocol (Jan 2022) — use https:// for the source dependency.
lazy val buildPlugin         = RootProject(uri("https://github.com/lift/sbt-lift-build.git#f9c52bda7b43a98b9f8805c654c713d99db0a58f"))
lazy val root = (project in file(".")).dependsOn(buildPlugin)

addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.4.2")
