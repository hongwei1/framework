# lift-persistence

A maintenance fork of the [Lift Framework](https://github.com/lift/framework)'s persistence layer, stripped down to a single artifact.

Upstream Lift 4.0.0 removed all persistence modules (`lift-mapper`, `lift-db`, `lift-proto`). This fork keeps them alive — without the web framework — for projects that still rely on Mapper as their ORM.

## What's in the artifact

One module, `lift-persistence`, merging the former:

- `lift-common` — `Box`, `Logging`, and core abstractions
- `lift-util` — helpers, props, security utilities
- `lift-db` — JDBC abstraction and connection management
- `lift-proto` — `ProtoUser` and friends
- `lift-mapper` — the Mapper ORM

## What was removed

- All web modules (`lift-webkit`, templating, Comet, SiteMap, …)
- `lift-json` — consumers should migrate to [json4s](https://github.com/json4s/json4s)
- `lift-actor` and `LAFuture`
- `lift-markdown`

## Usage

Via [JitPack](https://jitpack.io):

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.hongwei1.framework</groupId>
  <artifactId>lift-persistence_2.12</artifactId>
  <version>lift-persistence-SNAPSHOT</version>
</dependency>
```

## Building

```
sbt clean +test +publishM2
```

Requires JDK 8+ and Scala 2.12 / 2.13 (cross-built).

## License

Apache License 2.0 — see [LICENSE.txt](LICENSE.txt). Original code copyright WorldWide Conferencing, LLC.
