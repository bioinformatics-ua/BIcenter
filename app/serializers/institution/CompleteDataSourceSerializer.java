package serializers.institution;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.DataSource;

import java.io.IOException;

public class CompleteDataSourceSerializer extends JsonSerializer<DataSource> {
    @Override
    public void serialize(DataSource value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeStringField("institution", value.getInstitution().getName());
        gen.writeStringField("connectionName", value.getConnectionName());

        if(value.getDatabaseInterface() != null)
            gen.writeStringField("databaseInterface", value.getDatabaseInterface());

        gen.writeNumberField("accessType", value.getAccessType());

        if(value.getHostname() != null)
            gen.writeStringField("hostname", value.getHostname());

        gen.writeNumberField("portNumber", value.getPortNumber());

        if(value.getDatabaseName() != null)
            gen.writeStringField("databaseName", value.getDatabaseName());

        if(value.getUsername() != null)
            gen.writeStringField("username", value.getUsername());

        if(value.getPassword() != null)
            gen.writeStringField("password", value.getPassword());

        // Finish output
        gen.writeEndObject();
    }
}
