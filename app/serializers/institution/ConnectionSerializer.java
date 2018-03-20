package serializers.institution;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.DataSource;

import java.io.IOException;

public class ConnectionSerializer extends JsonSerializer<DataSource> {
    @Override
    public void serialize(DataSource value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());
        gen.writeStringField("connectionName", value.getConnectionName());

        // Finish output
        gen.writeEndObject();
    }
}
