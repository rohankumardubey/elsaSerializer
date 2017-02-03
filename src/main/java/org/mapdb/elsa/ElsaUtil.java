package org.mapdb.elsa;

import java.io.*;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Utilities for Elsa Serialization
 */
public final class ElsaUtil {

    private ElsaUtil(){}

    /**
     * Unpack int value from the input stream.
     *
     * @param is The input stream.
     * @return The long value.
     *
     * @throws java.io.IOException in case of IO error
     */
    static public int unpackInt(DataInput is) throws IOException {
        int ret = 0;
        byte v;
        do{
            v = is.readByte();
            ret = (ret<<7 ) | (v & 0x7F);
        }while((v&0x80)==0);

        return ret;
    }

    /**
     * Unpack long value from the input stream.
     *
     * @param in The input stream.
     * @return The long value.
     *
     * @throws java.io.IOException in case of IO error
     */
    static public long unpackLong(DataInput in) throws IOException {
        long ret = 0;
        byte v;
        do{
            v = in.readByte();
            ret = (ret<<7 ) | (v & 0x7F);
        }while((v&0x80)==0);

        return ret;
    }


    /**
     * Unpack int value from the input stream.
     *
     * @param in The input stream.
     * @return The long value.
     *
     * @throws java.io.IOException in case of IO error
     */
    static public int unpackInt(InputStream in) throws IOException {
        int ret = 0;
        int v;
        do{
            v = in.read();
            if(v==-1)
                throw new EOFException();
            ret = (ret<<7 ) | (v & 0x7F);
        }while((v&0x80)==0);

        return ret;
    }


    /**
     * Unpack long value from the input stream.
     *
     * @param in The input stream.
     * @return The long value.
     *
     * @throws java.io.IOException in case of IO error
     */
    static public long unpackLong(InputStream in) throws IOException {
        long ret = 0;
        int v;
        do{
            v = in.read();
            if(v==-1)
                throw new EOFException();
            ret = (ret<<7 ) | (v & 0x7F);
        }while((v&0x80)==0);

        return ret;
    }

    /**
     * Pack long into output.
     * It will occupy 1-10 bytes depending on value (lower values occupy smaller space)
     *
     * @param out DataOutput to put value into
     * @param value to be serialized, must be non-negative
     *
     * @throws java.io.IOException in case of IO error
     */
    static public void packLong(DataOutput out, long value) throws IOException {
        //$DELAY$
        int shift = 63-Long.numberOfLeadingZeros(value);
        shift -= shift%7; // round down to nearest multiple of 7
        while(shift!=0){
            out.writeByte((byte) ((value>>>shift) & 0x7F) );
            //$DELAY$
            shift-=7;
        }
        out.writeByte((byte) ((value & 0x7F)|0x80));
    }


    /**
     * Pack long into output.
     * It will occupy 1-10 bytes depending on value (lower values occupy smaller space)
     *
     * @param out OutputStream to put value into
     * @param value to be serialized, must be non-negative
     *
     * @throws java.io.IOException in case of IO error
     */
    static public void packLong(OutputStream out, long value) throws IOException {
        //$DELAY$
        int shift = 63-Long.numberOfLeadingZeros(value);
        shift -= shift%7; // round down to nearest multiple of 7
        while(shift!=0){
            out.write((int) ((value>>>shift) & 0x7F));
            //$DELAY$
            shift-=7;
        }
        out.write((int) ((value & 0x7F)|0x80));
    }


    /**
     * Pack int into an output stream.
     * It will occupy 1-5 bytes depending on value (lower values occupy smaller space)
     *
     * @param out DataOutput to put value into
     * @param value to be serialized, must be non-negative
     * @throws java.io.IOException in case of IO error
     */

    static public void packInt(DataOutput out, int value) throws IOException {
        // Optimize for the common case where value is small. This is particular important where our caller
        // is ElsaSerializerBase.SER_STRING.serialize because most chars will be ASCII characters and hence in this range.
        // credit Max Bolingbroke https://github.com/jankotek/MapDB/pull/489

        int shift = (value & ~0x7F); //reuse variable
        if (shift != 0) {
            //$DELAY$
            shift = 31-Integer.numberOfLeadingZeros(value);
            shift -= shift%7; // round down to nearest multiple of 7
            while(shift!=0){
                out.writeByte((byte) ((value>>>shift) & 0x7F));
                //$DELAY$
                shift-=7;
            }
        }
        //$DELAY$
        out.writeByte((byte) ((value & 0x7F)|0x80));
    }

    /**
     * Pack int into an output stream.
     * It will occupy 1-5 bytes depending on value (lower values occupy smaller space)
     *
     * This method is same as {@link #packInt(DataOutput, int)},
     * but is optimized for values larger than 127. Usually it is recids.
     *
     * @param out String to put value into
     * @param value to be serialized, must be non-negative
     * @throws java.io.IOException in case of IO error
     */

    static public void packIntBigger(DataOutput out, int value) throws IOException {
        //$DELAY$
        int shift = 31-Integer.numberOfLeadingZeros(value);
        shift -= shift%7; // round down to nearest multiple of 7
        while(shift!=0){
            out.writeByte((byte) ((value>>>shift) & 0x7F));
            //$DELAY$
            shift-=7;
        }
        //$DELAY$
        out.writeByte((byte) ((value & 0x7F)|0x80));
    }


    /**
     * Serializes content of iterable to find unknown classes.
     * That can be passed to {@link ElsaMaker#registerClasses(Class[])}
     * }
     * @param e iterable over objects, whose object graph will be checked
     * @return array of unknown classes found in object graph
     */
    static public Class[] findUnknownClassesInCollection(Iterable e){
        final Set<Class> classes = new TreeSet(new Comparator<Class>() {
            @Override
            public int compare(Class o1, Class o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        ElsaSerializerPojo p = new ElsaSerializerPojo(null, 0, null, null, null, null, new ElsaClassCallback() {
            @Override
            public void classMissing(Class clazz) {
                classes.add(clazz);
            }
        }, null);
        for(Object o:e){
            try {
                p.serialize(new DataOutputStream(new ByteArrayOutputStream()), o);
            } catch (IOException e1) {
                throw new IOError(e1);
            }
        }
        return classes.toArray(new Class[0]);
    }

    static public Class[] findUnknownClasses(Object e){
        LinkedList l = new LinkedList();
        l.add(e);
        return findUnknownClassesInCollection(l);
    }
}