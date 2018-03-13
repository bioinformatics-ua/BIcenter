package serializers.performance;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import models.DataRow;
import models.KeyValue;

import java.io.IOException;

public class DataRowSerializer extends JsonSerializer<DataRow> {
    @Override
    public void serialize(DataRow value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Setup object mapper
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        JsonSerializer<Object>  serializer = serializers.findValueSerializer(KeyValue.class);
        module.addSerializer(KeyValue.class, serializer);

        mapper.registerModule(module);

        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeStringField("stepName", value.getStep().getLabel());
        gen.writeNumberField("nrRow", value.getNrRow());

        gen.writeObjectField("keyValues", mapper.valueToTree(value.getKeyValues()));

        // Finish output
        gen.writeEndObject();
    }
}
