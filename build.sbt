lazy val pshot = (project in file("."))
  .settings(
     name := "pshot",
     version := "0.1-SNAPSHOT",
     scalaVersion := "2.12.1",
     scalacOptions ++= Seq("-deprecation", "-explaintypes", "-feature",
                           "-unchecked", "-Xfuture", "-Xlint"),
     assemblyMergeStrategy in assembly := {
       case "application.conf" | "reference.conf" => MergeStrategy.discard
       case x =>
         val old = (assemblyMergeStrategy in assembly).value
         old(x)
     },
     libraryDependencies ++= Seq(
       "com.github.kxbmap" %% "configs" % "0.4.4"
     )
   )
