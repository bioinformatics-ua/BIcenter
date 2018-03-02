package serializers.step;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.pentaho.di.core.row.ValueMetaInterface;

import java.io.IOException;

public class ValueMetaInterfaceInputFields extends JsonSerializer<ValueMetaInterface> {
    @Override
    public void serialize(ValueMetaInterface value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartObject();

        gen.writeStringField("name", value.getName());
        gen.writeStringField("origin", value.getOrigin());

        gen.writeEndObject();
    }
}
