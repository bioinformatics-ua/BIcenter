package serializers.performance;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import models.*;
import org.apache.batik.anim.timing.Interval;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class ExecutionSerializer extends JsonSerializer<Execution> {
    @Override
    public void serialize(Execution value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Setup object mapper
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        JsonSerializer<Object>  serializer = serializers.findValueSerializer(StepMetric.class);
        module.addSerializer(StepMetric.class, serializer);

        serializer = serializers.findValueSerializer(Status.class);
        module.addSerializer(Status.class, serializer);

        serializer = serializers.findValueSerializer(DataRow.class);
        module.addSerializer(DataRow.class, serializer);

        serializer = serializers.findValueSerializer(KeyValue.class);
        module.addSerializer(KeyValue.class, serializer);

        mapper.registerModule(module);

        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeBooleanField("finished", value.isFinished());
        gen.writeNumberField("errors", value.getErrors());

        if(value.getServer() != null) {
            gen.writeStringField("server", value.getServer().getName());
        }
        else {
            gen.writeStringField("server", "Local");
        }
        gen.writeStringField("user", value.getUser().getName());

        if(value.getStartDate()!=null){
            Date startDateTime = value.getStartDate();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String startDate = formatter.format(startDateTime);
            String startTime = String.format("%02d:%02d", startDateTime.getHours(),startDateTime.getMinutes());

            gen.writeStringField("startDate", startDate);
            gen.writeStringField("startTime", startTime);
        }
        if(value.getEndDate()!=null){
            Date endDateTime = value.getEndDate();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String endDate = formatter.format(endDateTime);
            String endTime = String.format("%02d:%02d", endDateTime.getHours(), endDateTime.getMinutes());

            gen.writeStringField("endDate", endDate);
            gen.writeStringField("endTime", endTime);
        }
        if(value.getDuration()!=null) {
            PeriodFormatter daysHoursMinutes = new PeriodFormatterBuilder()
                    .appendDays()
                    .appendSuffix(" day", " days")
                    .appendSeparator(","," and ")
                    .appendMinutes()
                    .appendSuffix(" minute", " minutes")
                    .appendSeparator(", ", " and ")
                    .appendSeconds()
                    .appendSuffix(" second", " seconds")
                    .toFormatter();

            gen.writeStringField("duration", daysHoursMinutes.print(value.getDuration().normalizedStandard()));
        }
        gen.writeStringField("logText", value.getLog());

        gen.writeObjectField("stepMetrics", mapper.valueToTree(value.getStepMetrics()));
        gen.writeObjectField("stepStatus", mapper.valueToTree(value.getStepStatus()));
        gen.writeObjectField("dataRows", mapper.valueToTree(value.getDataRows()));

        // Finish output
        gen.writeEndObject();
    }
}
