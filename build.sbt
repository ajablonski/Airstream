enablePlugins(ScalaJSPlugin)

enablePlugins(ScalaJSBundlerPlugin)

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "1.1.0", // This has no runtime cost. We only use it for `Debug.log` // @TODO[Elegance] Reconsider
  "org.scalatest" %%% "scalatest" % "3.2.0" % Test
)

scalacOptions ++= Seq(
  // "-deprecation",
  "-feature",
  "-language:higherKinds",
  "-language:implicitConversions"
)

// @TODO[Build] Why does this need " in (Compile, doc)" while other options don't?
scalacOptions in (Compile, doc) ++= Seq(
  "-no-link-warnings" // Suppress scaladoc "Could not find any member to link for" warnings
)

val generateTupleCombinatorsFrom = 3
val generateTupleCombinatorsTo = 22

Compile / sourceGenerators += Def.task {
  Seq.concat(
    (generateTupleCombinatorsFrom to generateTupleCombinatorsTo).flatMap(n =>
      new CombineSignalGenerator((Compile / sourceManaged).value, n).generate()
    ),
    (generateTupleCombinatorsFrom to generateTupleCombinatorsTo).flatMap(n =>
      new CombineEventStreamGenerator((Compile / sourceManaged).value, n).generate()
    ),
    new SignalCombinesGenerator((Compile / sourceManaged).value, from = generateTupleCombinatorsFrom, to = generateTupleCombinatorsTo).generate(),
    new EventStreamCombinesGenerator((Compile / sourceManaged).value, from = generateTupleCombinatorsFrom, to = generateTupleCombinatorsTo).generate(),
    new TupleGenerator((Compile / sourceManaged).value).generate(),
    new NonTupleGenerator((Compile / sourceManaged).value).generate(),
    new TupleCompositionGenerator((Compile / sourceManaged).value).generate()
  )
}.taskValue

Test / sourceGenerators += Def.task {
  Seq.concat(
    new CombineSignalTestGenerator((Test / sourceManaged).value, from = generateTupleCombinatorsFrom, to = generateTupleCombinatorsTo).generate(),
    new CombineEventStreamTestGenerator((Test / sourceManaged).value, from = generateTupleCombinatorsFrom, to = generateTupleCombinatorsTo).generate(),
    new CompositionTestGenerator((Test / sourceManaged).value).generate()
  )
}.taskValue

mappings in (Compile, packageSrc) ++= {
  val base  = (sourceManaged in Compile).value
  val files = (managedSources in Compile).value
  files.map { f =>
    (f, f.relativeTo(base / "scala").get.getPath)
  }
}

version in installJsdom := "16.4.0"

useYarn := true

requireJsDomEnv in Test := true

parallelExecution in Test := false

scalaJSUseMainModuleInitializer := true

scalaJSLinkerConfig in (Compile, fastOptJS) ~= { _.withSourceMap(false) }
