package io.github.abdulmajid.LogFlux;

import io.github.abdulmajid.LogFlux.kafka.KafKaFlinkConsumer;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello world!");
        KafKaFlinkConsumer.startConsuming();
    }
}