package com.example.demo;

import com.nixxcode.jvmbrotli.enc.BrotliOutputStream;
import org.apache.commons.compress.compressors.brotli.BrotliCompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import static org.springframework.test.util.AssertionErrors.assertEquals;
/*
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CompressionBenchmark {

    static String TEXT_DATA;


    // Initialize binary data with random bytes
    static {
        byte[] array = new byte[1024 * 120]; // length is bounded by 7
        new Random().nextBytes(array);
        TEXT_DATA = new String(array, Charset.forName("UTF-8"));

    }



    @Benchmark
    public void brotliCompression(Blackhole blackhole) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BrotliCompressorO brotliOutputStream = new BrotliCompressorInputStream(new BufferedInputStream(baos));
        brotliOutputStream.write(TEXT_DATA.getBytes());
        brotliOutputStream.close();
        blackhole.consume(baos.toByteArray());

        try (OutputStream outputStream = new FileOutputStream(compressedFile);
             BrotliCompressorOutputStream compressorOutputStream = new BrotliCompressorOutputStream(outputStream)) {
            compressorOutputStream.write(inputData);
        }

        // Decompression
        byte[] decompressedData;
        try (InputStream inputStream = new FileInputStream(compressedFile);
             BrotliCompressorInputStream decompressorInputStream = new BrotliCompressorInputStream(inputStream)) {
            decompressedData = decompressorInputStream.readAllBytes();
        }

        // Convert decompressed data back to a string
        String decompressedString = new String(decompressedData);

        // Verify that the decompressed data matches the original data
        assertEquals(originalData, decompressedString);
    }


    @Test
    void runBenchmarks() throws RunnerException {
        Options options = new OptionsBuilder()
                .measurementIterations(3)
                .warmupIterations(1)
                .forks(1)
                .verbosity(VerboseMode.NORMAL)
                .include(getClass().getSimpleName())
                .build();

        new Runner(options).run();
    }
    */


