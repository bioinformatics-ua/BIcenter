package serializers.step;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;

import java.io.IOException;
import java.util.Map;

public class MapInputFields extends JsonSerializer<Map.Entry<String,RowMetaInterface>> {
    @Override
    public void serialize(Map.Entry<String, RowMetaInterface> value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Setup object mapper
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        JsonSerializer<Object> serializer = serializers.findValueSerializer(ValueMetaInterface.class);
        module.addSerializer(ValueMetaInterface.class, serializer);

        mapper.registerModule(module);

        // Start composing output
        gen.writeStartObject();

        gen.writeStringField("step", value.getKey());

        gen.writeObjectField("valueMetas", mapper.valueToTree(value.getValue()));

        // Finish output
        gen.writeEndObject();
    }
}
