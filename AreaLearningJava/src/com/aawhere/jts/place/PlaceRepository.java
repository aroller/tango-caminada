package com.aawhere.jts.place;

import android.util.Log;

import com.google.common.io.Closeables;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by aroller on 7/2/15.
 */
public class PlaceRepository {

    private static final String TAG = PlaceRepository.class.getSimpleName();
    private final String mapId;
    private final File file;
    private HashMap<Long, Place> places;

    public PlaceRepository(String mapId, File filesDir) {
        this.mapId = mapId;

        this.file = new File(new File(filesDir, "places"), mapId);
        try {
            FileInputStream fileInput = new FileInputStream(this.file);
            ObjectInputStream input = new ObjectInputStream(fileInput);
            this.places = (HashMap<Long, Place>) input.readObject();
        } catch (FileNotFoundException e) {
            Log.i(TAG, "empty repository since no file found " + e.getMessage());
            this.places = new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            Log.e(TAG, "empty repository since " + e.getMessage());
            this.places = new HashMap<>();
        }
    }

    /**
     * @param id
     * @return the place matching the id or null if none are found
     */
    public Place place(Long id) {
        return places.get(id);
    }

    /**
     * Adds a place to the repository available for retrieval during this session and others.
     *
     * @param place
     */
    public void add(Place place) {
        this.places.put(place.id(), place);
        try {
            write();
        } catch (IOException e) {
            Log.e(TAG, "unable to save " + e.getMessage());
        }
    }

    public Collection<Place> all() {
        return places.values();
    }

    private void write() throws IOException {
        this.file.getParentFile().mkdirs();

        FileOutputStream fileOut = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            fileOut = new FileOutputStream(this.file);
            objectOutputStream = new ObjectOutputStream(fileOut);
            objectOutputStream.writeObject(places);
        } finally {
            Closeables.close(objectOutputStream, true);
            Closeables.close(fileOut, true);

        }

    }


}
