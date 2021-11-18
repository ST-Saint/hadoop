# fuzz sample

## prerequisite

-   afl

```bash
# Clone and build AFL
git clone https://github.com/google/afl && (cd afl && make)

# Set AFL directory location
export AFL_DIR=$(pwd)/afl
```

-   jqf

```bash
# Clone and build JQF
git clone https://github.com/rohanpadhye/jqf && jqf/setup.sh

### See usage of JQF-AFL
jqf/bin/jqf-afl-fuzz
```

## test

-   build

```bash
mvn -Pdist -DskipTests clean compile test-compile dependency:copy-dependencies package
```

- compile

```bash
mvn compile test-compile
```

-   run

```bash
./fuzz_jqf.sh
```
