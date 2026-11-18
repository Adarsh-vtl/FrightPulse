package com.freightpulse.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HaversineUtilTest {

    @Test
    void samePointReturnsZero() {
        double dist = HaversineUtil.distance(28.6139, 77.2090, 28.6139, 77.2090);
        assertEquals(0.0, dist, 0.0001);
    }

    @Test
    void delhiToMumbaiDistanceIsAround1150Km() {
        // Delhi: 28.6139, 77.2090; Mumbai: 19.0760, 72.8777
        double dist = HaversineUtil.distance(28.6139, 77.2090, 19.0760, 72.8777);
        assertTrue(dist >= 1140.0 && dist <= 1160.0, "Expected ~1150 km but got " + dist);
    }
}
