package org.ufl.hypogator.jackb.traversers.conceptnet;

import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import org.ufl.hypogator.jackb.utils.adt.HashMultimapSerializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class ConceptNet5CSVDump {

    public static void main(String args[]) throws IOException {
        File folder = new File("/media/giacomo/Biggus/project_dir/data/postgres");
        Gson g = new Gson();

        {
            HashMultimap<String, String> clangToSeed;
            File a = new File(folder, "clangToSeed.ser");
            clangToSeed = HashMultimapSerializer.unserialize(a);
            FileWriter fw = new FileWriter("/media/giacomo/Biggus/project_dir/data/postgres/clangToSeed.json");
            JsonWriter writer = new JsonWriter(fw);
            writer.beginObject();
            clangToSeed.asMap().forEach((k, v) -> {
                try {
                    writer.name(k);
                    writer.beginArray();
                    for (String x : v) {
                        writer.value(x);
                    }
                    writer.endArray();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            });
            writer.endObject();
            writer.close();
        }

        {
            File b = new File(folder, "idToOffset.ser");
            Map<String, Long> idToOffset;
            idToOffset = HashMultimapSerializer.unserializeMap(b);
            FileWriter fw = new FileWriter("/media/giacomo/Biggus/project_dir/data/postgres/idToOffset.json");
            JsonWriter writer = new JsonWriter(fw);
            writer.beginObject();
            idToOffset.forEach((k, v) -> {
                try {
                    writer.name(k);
                    writer.value(v);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            });
            writer.endObject();
            writer.close();
        }
    }

}
