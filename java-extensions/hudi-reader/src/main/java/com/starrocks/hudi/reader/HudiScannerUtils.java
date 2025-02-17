// Copyright 2021-present StarRocks, Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.starrocks.hudi.reader;

import com.starrocks.jni.connector.ColumnType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HudiScannerUtils {
    private static final DateTimeFormatter DATETIME_FORMATTER;
    public static final Map<String, String> MARK_TYPE_VALUE_MAPPING = new HashMap<>();
    public static Map<ColumnType.TypeValue, TimeUnit> TIMESTAMP_UNIT_MAPPING = new HashMap<>();

    static {
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        // Date and time parts
        builder.append(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        DATETIME_FORMATTER = builder.toFormatter();

        MARK_TYPE_VALUE_MAPPING.put("TimestampMicros", "timestamp");
        MARK_TYPE_VALUE_MAPPING.put("TimestampMillis", "timestamp");

        TIMESTAMP_UNIT_MAPPING.put(ColumnType.TypeValue.DATETIME_MICROS, TimeUnit.MICROSECONDS);
        TIMESTAMP_UNIT_MAPPING.put(ColumnType.TypeValue.DATETIME_MILLIS, TimeUnit.MILLISECONDS);
    }

    private static final long MILLI = 1000;
    private static final long MICRO = 1_000_000;
    private static final long NANO = 1_000_000_000;

    public static LocalDateTime getTimestamp(long value, TimeUnit timeUnit, boolean isAdjustedToUTC) {

        ZoneId zone = ZoneOffset.UTC;
        if (isAdjustedToUTC) {
            zone = ZoneId.systemDefault();
        }
        long seconds = 0L;
        long nanoseconds = 0L;

        switch (timeUnit) {
            case MILLISECONDS:
                seconds = value / MILLI;
                nanoseconds = (value % MILLI) * MICRO;
                break;

            case MICROSECONDS:
                seconds = value / MICRO;
                nanoseconds = (value % MICRO) * MILLI;
                break;

            case NANOSECONDS:
                seconds = value / NANO;
                nanoseconds = (value % NANO);
                break;
            default:
                break;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(seconds, nanoseconds), zone);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }

    public static boolean isInt64Timestamp(ColumnType.TypeValue type) {
        return (type == ColumnType.TypeValue.DATETIME_MICROS
                || type == ColumnType.TypeValue.DATETIME_MILLIS);
    }
}