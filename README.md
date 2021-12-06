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
mvn clean compile test-compile && mvn dependency:copy-dependencies
mvn -Pdist -DskipTests package
```

- compile

```bash
mvn compile test-compile
```

-   run

```bash
./fuzz_jqf.sh
```
