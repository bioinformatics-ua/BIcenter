package serializers.performance;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.KeyValue;
import models.Status;

import java.io.IOException;

public class KeyValueSerializer extends JsonSerializer<KeyValue> {
    @Override
    public void serialize(KeyValue value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeStringField("key", value.getColKey());
        gen.writeStringField("value", value.getColValue());

        // Finish output
        gen.writeEndObject();
    }
}
