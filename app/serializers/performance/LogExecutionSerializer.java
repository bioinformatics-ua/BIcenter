package serializers.performance;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.Execution;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.io.IOException;

public class LogExecutionSerializer extends JsonSerializer<Execution> {
    @Override
    public void serialize(Execution value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeBooleanField("finished", value.isFinished());
        gen.writeStringField("logText", value.getLog());

        // Finish output
        gen.writeEndObject();
    }
}
