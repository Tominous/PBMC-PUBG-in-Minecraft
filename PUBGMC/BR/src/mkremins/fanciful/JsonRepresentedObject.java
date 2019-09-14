package mkremins.fanciful;

import java.io.IOException;

import com.google.gson.stream.JsonWriter;

interface JsonRepresentedObject
{
    void writeJson(final JsonWriter p0) throws IOException;
}
