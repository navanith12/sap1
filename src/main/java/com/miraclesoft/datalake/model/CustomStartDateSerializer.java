package com.miraclesoft.datalake.model;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomStartDateSerializer extends StdSerializer<Date> {

    protected CustomStartDateSerializer() {
        this(null);
    }

    protected CustomStartDateSerializer(Class<Date> t) {
        super(t);
    }

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        jsonGenerator.writeString(dateFormat.format(date));
    }
}