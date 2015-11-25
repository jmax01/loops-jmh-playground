package com.takipi.oss.benchmarks.jmh.loops;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@SuppressWarnings("javadoc")
@State(Scope.Benchmark)
public class LoopBenchmarkMain {

    static final int size = 100000;
    // volatile int size = 100000;

    volatile List<Integer> integers = null;

    public static void main(String[] args) {
        LoopBenchmarkMain benchmark = new LoopBenchmarkMain();
        benchmark.setup();

        System.out.println("iteratorMaxInteger max is: " + benchmark.iteratorMaxInteger());
        System.out.println("forEachLoopMaxInteger max is: " + benchmark.forEachLoopMaxInteger());
        System.out.println("forEachLambdaMaxInteger max is: " + benchmark.forEachLambdaMaxInteger());
        System.out.println("forMaxInteger max is: " + benchmark.forMaxInteger());
        System.out.println("parallelStreamMaxInteger max is: " + benchmark.parallelStreamMaxInteger());
        System.out.println("streamMaxInteger max is: " + benchmark.streamMaxInteger());
        System.out.println("iteratorMaxInteger max is: " + benchmark.lambdaMaxInteger());

        System.out.println("iteratorMaxInteger max is: " + benchmark.parallelIntStreamMaxInteger());
    }

    @Setup
    public void setup() {
        this.integers = new ArrayList<Integer>(LoopBenchmarkMain.size);
        this.populate(this.integers);
    }

    public void populate(List<Integer> list) {
        Random random = new Random();
        for (int i = 0; i < LoopBenchmarkMain.size; i++) {
            list.add(random.nextInt(1000000));
        }
    }

    /**
     * Converted to true iterator
     *
     * @return the int
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(2)
    @Measurement(iterations = 5)
    @Warmup(iterations = 5)
    public int iteratorMaxInteger() {
        int max = Integer.MIN_VALUE;
        final Iterator<Integer> iterator = this.integers.iterator();
        while (iterator.hasNext()) {
            max = Integer.max(max, iterator.next());
        }
        return max;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(2)
    @Measurement(iterations = 5)
    @Warmup(iterations = 5)
    public int forEachLoopMaxInteger() {
        int max = Integer.MIN_VALUE;

        final List<Integer> ints = this.integers;

        for (Integer n : ints) {
            max = Integer.max(max, n);
        }
        return max;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(2)
    @Measurement(iterations = 5)
    @Warmup(iterations = 5)
    public int forEachLambdaMaxInteger() {
        final Wrapper wrapper = new Wrapper();
        wrapper.inner = Integer.MIN_VALUE;

        final List<Integer> ints = this.integers;

        ints.forEach(i -> this.helper(i, wrapper));
        return wrapper.inner.intValue();
    }

    public static class Wrapper {
        public Integer inner;
    }

    private int helper(int i, Wrapper wrapper) {
        wrapper.inner = Math.max(i, wrapper.inner);
        return wrapper.inner;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(2)
    @Measurement(iterations = 5)
    @Warmup(iterations = 5)
    public int forEachLambdaMaxInteger2() {

        final Wrapper2 wrapper2 = new Wrapper2();

        final List<Integer> ints = this.integers;

        ints.forEach(i -> this.helper(i, wrapper2));

        return wrapper2.inner.intValue();
    }

    public static final class Wrapper2 {
        public Integer inner = Integer.MIN_VALUE;
    }

    private int helper(final int i, final Wrapper2 wrapper) {
        wrapper.inner = Math.max(i, wrapper.inner);
        return wrapper.inner;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(2)
    @Measurement(iterations = 5)
    @Warmup(iterations = 5)
    public int forMaxInteger() {
        int max = Integer.MIN_VALUE;

        final List<Integer> ints = this.integers;

        for (int i = 0; i < LoopBenchmarkMain.size; i++) {
            max = Integer.max(max, ints.get(i));
        }
        return max;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(2)
    @Measurement(iterations = 5)
    @Warmup(iterations = 5)
    public int parallelStreamMaxInteger() {

        final List<Integer> ints = this.integers;

        Optional<Integer> max = ints.parallelStream()
            .reduce(Integer::max);
        return max.get();
    }

    /**
     * <pre>
     * LoopBenchmarkMain.forEachLambdaMaxInteger      avgt   10  0.528 ± 0.013  ms/op
     * LoopBenchmarkMain.forEachLoopMaxInteger        avgt   10  0.119 ± 0.008  ms/op
     * LoopBenchmarkMain.forMaxInteger                avgt   10  0.217 ± 0.003  ms/op
     * LoopBenchmarkMain.iteratorMaxInteger           avgt   10  0.124 ± 0.004  ms/op
     * LoopBenchmarkMain.lambdaMaxInteger             avgt   10  0.533 ± 0.023  ms/op
     * LoopBenchmarkMain.parallelIntStreamMaxInteger  avgt   10  0.106 ± 0.009  ms/op
     * LoopBenchmarkMain.parallelStreamMaxInteger     avgt   10  0.426 ± 0.096  ms/op
     * LoopBenchmarkMain.streamMaxInteger             avgt   10  0.634 ± 0.009  ms/op
     * </pre>
     *
     * @return the int
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(2)
    @Measurement(iterations = 5)
    @Warmup(iterations = 5)
    public int parallelIntStreamMaxInteger() {

        final List<Integer> ints = this.integers;

        return ints.parallelStream()
            .mapToInt(e -> e)
            .max()
            .getAsInt();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(2)
    @Measurement(iterations = 5)
    @Warmup(iterations = 5)
    public int streamMaxInteger() {

        final List<Integer> ints = this.integers;

        Optional<Integer> max = ints.stream()
            .reduce(Integer::max);
        return max.get();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(2)
    @Measurement(iterations = 5)
    @Warmup(iterations = 5)
    public int intStreamMaxInteger() {

        final List<Integer> ints = this.integers;

        return ints.stream()
            .mapToInt(e -> e)
            .max()
            .getAsInt();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(2)
    @Measurement(iterations = 5)
    @Warmup(iterations = 5)
    public int lambdaMaxInteger() {

        final List<Integer> ints = this.integers;

        return ints.stream()
            .reduce(Integer.MIN_VALUE, (a, b) -> Integer.max(a, b));
    }
}
