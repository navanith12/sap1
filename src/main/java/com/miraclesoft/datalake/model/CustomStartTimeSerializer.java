package com.miraclesoft.datalake.model;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CustomStartTimeSerializer extends StdSerializer<Date> {

    protected CustomStartTimeSerializer() {
        this(null);
    }

    protected CustomStartTimeSerializer(Class<Date> t) {
        super(t);
    }

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Eastern Standard Time"));
        jsonGenerator.writeString(dateFormat.format(date));
    }
}