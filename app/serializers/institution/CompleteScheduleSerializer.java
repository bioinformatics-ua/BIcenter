package serializers.institution;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.Schedule;

import java.io.IOException;

public class CompleteScheduleSerializer  extends JsonSerializer<Schedule> {
    @Override
    public void serialize(Schedule value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeStringField("institution", value.getInstitution().getName());
        gen.writeNumberField("taskId", value.getTask().getId());
        gen.writeStringField("task", value.getTask().getName());
        gen.writeStringField("server", value.getServer().getName());

        if(value.getStart().toString()!=null)
            gen.writeStringField("start", value.getStart().toString());

        if(value.getInterval()!=null)
            gen.writeStringField("interval", value.getInterval());

        // Finish output
        gen.writeEndObject();
    }
}
