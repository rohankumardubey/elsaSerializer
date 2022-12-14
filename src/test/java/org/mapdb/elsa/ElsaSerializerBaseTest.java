/******************************************************************************
 * Copyright 2010 Cees De Groot, Alex Boisvert, Jan Kotek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.mapdb.elsa;

import org.junit.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;

import static org.junit.Assert.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ElsaSerializerBaseTest {


    @Test public void testInt() throws IOException{
        int[] vals = {
                Integer.MIN_VALUE,
                2*Short.MIN_VALUE,
                -1+Short.MIN_VALUE,
                256*Short.MIN_VALUE,
                Short.MIN_VALUE,
                -10, -9, -8, -7, -6, -5, -4, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                127, 254, 255, 256, Short.MAX_VALUE, Short.MAX_VALUE + 1,
                Short.MAX_VALUE * 2, Integer.MAX_VALUE,256*Short.MIN_VALUE,
                0x80FFFFFF //Issue #202
        };
        for (Integer i : vals) {
            Object l2 = clone(i);
            assertEquals(i, l2);
            assertTrue(l2.getClass() == Integer.class);
        }
    }

    void serSize(int expected, Object val) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream out2 = new DataOutputStream(out);
        new ElsaSerializerPojo().serialize(out2, val);
        assertEquals(expected, out.toByteArray().length);
    }

    @Test public void testIntSize() throws IOException {
        serSize(1,Integer.MIN_VALUE);
        serSize(1,Integer.MAX_VALUE);
        for(int i=-9;i<=16;i++)
            serSize(1,i);
        serSize(2, 100);
        serSize(2, -100);
        serSize(3, 0xFFF);
        serSize(3, -0xFFF);
        serSize(4, 0xFFFFF);
        serSize(4, -0xFFFFF);
        serSize(5, 0xFFFFFFF);
        serSize(5, -0xFFFFFFF);
    }

    @Test public void testShort() throws IOException{
        for (int i = Short.MIN_VALUE;i<=Short.MAX_VALUE;i++) {
            Short ii = (short)i;
            Object l2 = clone(ii);
            assertEquals(ii,l2);
            assertTrue(l2.getClass() == Short.class);
        }
    }

    @Test public void testDouble() throws IOException{
        double[] vals = {
                1f, 0f, -1f, Math.PI, 255, 256, Short.MAX_VALUE, Short.MAX_VALUE + 1, -100
        };
        for (double i : vals) {
            Object l2 = clone(i);
            assertTrue(l2.getClass() == Double.class);
            assertEquals(l2, i);
        }
    }


    @Test public void testFloat() throws IOException{
        float[] vals = {
                1f, 0f, -1f, (float) Math.PI, 255, 256, Short.MAX_VALUE, Short.MAX_VALUE + 1, -100
        };
        for (float i : vals) {
            Object l2 = clone(i);
            assertTrue(l2.getClass() == Float.class);
            assertEquals(l2, i);
        }
    }

    @Test public void testChar() throws IOException{
        for (int ii = Character.MIN_VALUE;ii<=Character.MAX_VALUE;ii++) {
            Character i = (char)ii;
            Object l2 = clone(i);
            assertEquals(l2.getClass(), Character.class);
            assertEquals(l2, i);
        }
    }


    @Test public void testLong() throws IOException{
        long[] vals = {
                65536,
                Long.MIN_VALUE,
                Integer.MIN_VALUE, (long)Integer.MIN_VALUE - 1, (long)Integer.MIN_VALUE + 1,
                2* Short.MIN_VALUE * 2,
                -1 + Short.MIN_VALUE,
                Short.MIN_VALUE,
                -10, -9, -8, -7, -6, -5, -4, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                127, 254, 255, 256, Short.MAX_VALUE, Short.MAX_VALUE + 1,
                Short.MAX_VALUE * 2, Integer.MAX_VALUE, (long)Integer.MAX_VALUE + 1, Long.MAX_VALUE,
                0x80FFFFFFFFFFFFFFL, //Issue #202
                0x8000000000000000L,
                0x7F00000000000001L

        };
        for (long i : vals) {
            Object l2 = clone(i);
            assertTrue(l2.getClass() == Long.class);
            assertEquals(l2, i);
        }
    }

    @Test public void testLongSize() throws IOException {
        serSize(1,Long.MIN_VALUE);
        serSize(1,Long.MAX_VALUE);
        for(long i=-9;i<=16;i++)
            serSize(1,i);
        serSize(2, 100L);
        serSize(2, -100L);
        serSize(3, 0xFFFL);
        serSize(3, -0xFFFL);
        serSize(4, 0xFFFFFL);
        serSize(4, -0xFFFFFL);
        serSize(5, 0xFFFFFFFL);
        serSize(5, -0xFFFFFFFL);
        serSize(6, 0xFFFFFFFFFL);
        serSize(6, -0xFFFFFFFFFL);
        serSize(7, 0xFFFFFFFFFFFL);
        serSize(7, -0xFFFFFFFFFFFL);
        serSize(8, 0xFFFFFFFFFFFFFL);
        serSize(8, -0xFFFFFFFFFFFFFL);
        serSize(9, 0xFFFFFFFFFFFFFFFL);
        serSize(9, -0xFFFFFFFFFFFFFFFL);
    }

    @Test public void testBoolean1() throws IOException{
        Object l2 = clone(true);
        assertTrue(l2.getClass() == Boolean.class);
        assertEquals(l2, true);

        Object l22 = clone(false);
        assertTrue(l22.getClass() == Boolean.class);
        assertEquals(l22, false);

    }

    @Test public void testString() throws IOException{
        String l2 = (String) clone("Abcd");
        assertEquals(l2, "Abcd");
    }

    @Test public void testBigString() throws IOException{
        String bigString = "";
        for (int i = 0; i < 1e4; i++) {
            bigString += i % 10;
            String l2 = clone(bigString);
            assertEquals(l2, bigString);
        }
    }


    @Test public void testNoArgumentConstructorInJavaSerialization() throws ClassNotFoundException, IOException {
        SimpleEntry a = new SimpleEntry(1, "11");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(a);
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
        SimpleEntry a2 = (SimpleEntry) in.readObject();
        assertEquals(a, a2);
    }


    @Test public void testArrayList() throws ClassNotFoundException, IOException {
        Collection c = new ArrayList();
        for (int i = 0; i < 1000; i++) {
            c.add(i);
            assertEquals(c, clone((c)));
        }
    }

    @Test public void testLinkedList() throws ClassNotFoundException, IOException {
        Collection c = new java.util.LinkedList();
        for (int i = 0; i < 2000; i++) {
            c.add(i);
            assertEquals(c, clone((c)));
        }
    }



    @Test public void testTreeSet() throws ClassNotFoundException, IOException {
        Collection c = new TreeSet();
        for (int i = 0; i < 2000; i++) {
            c.add(i);
            assertEquals(c, clone((c)));
        }
    }

    @Test public void testHashSet() throws ClassNotFoundException, IOException {
        Collection c = new HashSet();
        for (int i = 0; i < 2000; i++) {
            c.add(i);
            assertEquals(c, clone((c)));
        }
    }

    @Test public void testLinkedHashSet() throws ClassNotFoundException, IOException {
        Collection c = new LinkedHashSet();
        for (int i = 0; i < 2000; i++) {
            c.add(i);
            assertEquals(c, clone((c)));
        }
    }

    @Test public void testHashMap() throws ClassNotFoundException, IOException {
        Map c = new HashMap();
        for (int i = 0; i < 2000; i++) {
            c.put(i, i + 10000);
            assertEquals(c, clone((c)));
        }
    }

    @Test public void testTreeMap() throws ClassNotFoundException, IOException {
        Map c = new TreeMap();
        for (int i = 0; i < 2000; i++) {
            c.put(i, i + 10000);
            assertEquals(c, clone((c)));
        }
    }

    @Test public void testLinkedHashMap() throws ClassNotFoundException, IOException {
        Map c = new LinkedHashMap();
        for (int i = 0; i < 2000; i++) {
            c.put(i, i + 10000);
            assertEquals(c, clone((c)));
        }
    }


    @Test public void testProperties() throws ClassNotFoundException, IOException {
        Properties c = new Properties();
        for (int i = 0; i < 2000; i++) {
            c.put(i, i + 10000);
            assertEquals(c, clone((c)));
        }
    }


    @Test public void testClass() throws IOException{
        assertEquals(clone(String.class), String.class);
        assertEquals(clone(long[].class), long[].class);
    }


    @Test public void testUnicodeString() throws ClassNotFoundException, IOException {
        String s = "Ciudad Bol??va";
        assertEquals(clone(s), s);
    }

    @Test public void testPackedLongCollection() throws ClassNotFoundException, IOException {
        ArrayList l1 = new ArrayList();
        l1.add(0L);
        l1.add(1L);
        l1.add(0L);
        assertEquals(l1, clone((l1)));
        l1.add(-1L);
        assertEquals(l1, clone((l1)));
    }

    @Test public void testNegativeLongsArray() throws ClassNotFoundException, IOException {
       long[] l = new long[] { -12 };
       Object deserialize = clone((l));
       assertTrue(Arrays.equals(l, (long[]) deserialize));
     }


    @Test public void testNegativeIntArray() throws ClassNotFoundException, IOException {
       int[] l = new int[] { -12 };
       Object deserialize = clone((l));
       assertTrue(Arrays.equals(l, (int[]) deserialize));
     }


    @Test public void testNegativeShortArray() throws ClassNotFoundException, IOException {
       short[] l = new short[] { -12 };
       Object deserialize = clone((l));
        assertTrue(Arrays.equals(l, (short[]) deserialize));
     }

    @Test public void testBooleanArray() throws ClassNotFoundException, IOException {
        boolean[] l = new boolean[] { true,false };
        Object deserialize = clone((l));
        assertTrue(Arrays.equals(l, (boolean[]) deserialize));
    }

    @Test public void testBooleanArray3() throws ClassNotFoundException, IOException {
        boolean[] l = new boolean[] { true,false,false,false,true,true,false,false,false,false,true,true,false };
        Object deserialize = clone((l));
        assertTrue(Arrays.equals(l, (boolean[]) deserialize));
    }

    @Test public void testDoubleArray() throws ClassNotFoundException, IOException {
        double[] l = new double[] { Math.PI, 1D };
        Object deserialize = clone((l));
        assertTrue(Arrays.equals(l, (double[]) deserialize));
    }

    @Test public void testFloatArray() throws ClassNotFoundException, IOException {
        float[] l = new float[] { 1F, 1.234235F };
        Object deserialize = clone((l));
        assertTrue(Arrays.equals(l, (float[]) deserialize));
    }

    @Test public void testByteArray() throws ClassNotFoundException, IOException {
        byte[] l = new byte[] { 1,34,-5 };
        Object deserialize = clone((l));
        assertTrue(Arrays.equals(l, (byte[]) deserialize));
    }

    @Test public void testCharArray() throws ClassNotFoundException, IOException {
        char[] l = new char[] { '1','a','&' };
        Object deserialize = clone((l));
        assertTrue(Arrays.equals(l, (char[]) deserialize));
    }


    @Test public void testDate() throws IOException{
        Date d = new Date(6546565565656L);
        assertEquals(d, clone((d)));
        d = new Date(System.currentTimeMillis());
        assertEquals(d, clone((d)));
    }

    @Test public void testBigDecimal() throws IOException{
        BigDecimal d = new BigDecimal("445656.7889889895165654423236");
        assertEquals(d, clone((d)));
        d = new BigDecimal("-53534534534534445656.7889889895165654423236");
        assertEquals(d, clone((d)));
    }

    @Test public void testBigInteger() throws IOException{
        BigInteger d = new BigInteger("4456567889889895165654423236");
        assertEquals(d, clone((d)));
        d = new BigInteger("-535345345345344456567889889895165654423236");
        assertEquals(d, clone((d)));
    }


    @Test public void testUUID() throws IOException, ClassNotFoundException {
        //try a bunch of UUIDs.
        for(int i = 0; i < 1000;i++)
        {
            UUID uuid = UUID.randomUUID();
            assertEquals(uuid, clone((uuid)));
        }
    }

    @Test public void testArray() throws IOException {
        Object[] o = new Object[]{"A",Long.valueOf(1),Long.valueOf(2),Long.valueOf(3), Long.valueOf(3)};
        Object[] o2 = (Object[]) clone(o);
        assertTrue(Arrays.equals(o, o2));
    }


    @Test public void test_issue_38() throws IOException {
        String[] s = new String[5];
        String[] s2 = (String[]) clone(s);
        assertTrue(Arrays.equals(s, s2));
        assertTrue(s2.toString().contains("[Ljava.lang.String"));
    }

    @Test public void test_multi_dim_array() throws IOException {
        int[][] arr = new int[][]{{11,22,44},{1,2,34}};
        int[][] arr2= (int[][]) clone(arr);
        assertArrayEquals(arr, arr2);
    }

    @Test public void test_multi_dim_large_array() throws IOException {
        int[][] arr1 = new int[3000][];
        double[][] arr2 = new double[3000][];
        for(int i=0;i<3000;i++){
            arr1[i]= new int[]{i,i+1};
            arr2[i]= new double[]{i,i+1};
        }
        assertArrayEquals(arr1, clone(arr1));
        assertArrayEquals(arr2, clone(arr2));
    }


    @Test public void test_multi_dim_array2() throws IOException {
        Object[][] arr = new Object[][]{{11,22,44},{1,2,34}};
        Object[][] arr2= clone(arr);
        assertArrayEquals(arr, arr2);
    }


    private static final char[] chars = "0123456789abcdefghijklmnopqrstuvwxyz !@#$%^&*()_+=-{}[]:\",./<>?|\\".toCharArray();


    public static String randomString(int size) {
        return randomString(size, (int) (100000 * Math.random()));
    }

    public static String randomString(int size, int seed) {
        StringBuilder b = new StringBuilder(size);
        for(int i=0;i<size;i++){
            b.append(chars[Math.abs(seed)%chars.length]);
            seed = 31*seed;

        }
        return b.toString();
    }

    @Test public void test_strings_var_sizes() throws IOException {
        for(int i=0;i<50;i++){
            String s = randomString(i);
            assertEquals(s, clone((s)));
        }
    }


    @Test public void test_extended_chars() throws IOException {
        String s = "??????, ???????????????, ?????????????????????";
        assertEquals(s,clone((s)));
    }

    @Test public void testBooleanArray2() throws IOException {
        for(int i=0;i<1000;i++){
            boolean[] b = new boolean[i];
            for(int j=0;j<i;j++) b[j] = Math.random()<0.5;

            boolean[] b2 = (boolean[]) clone((b));

            for(int j=0;j<i;j++) assertEquals(b[j], b2[j]);
        }
    }

    /* clone value using serialization */
    static <E> E clone(E value) throws IOException {
        return clonePojo(value);
    }

    static <E> E clonePojo(E value) throws IOException {
        return (E) clonePojo(value, new ElsaSerializerPojo());
    }

    static <E> E clonePojo(E value, ElsaSerializerBase p) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream out2 = new DataOutputStream(out);
        p.serialize(out2, value);

        DataInputStream ins = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        return (E) p.deserialize(ins);
    }

    static <E> E cloneJava(E value) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream out2 = new ObjectOutputStream(out);
        out2.writeObject(value);
        out2.flush();
        out2.close();

        ObjectInputStream ins = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
        try {
            return (E) ins.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }



    @SuppressWarnings({  "rawtypes" })
    @Test public void testHeaderUnique() throws IllegalAccessException {
        ElsaSerializerBase b = new ElsaSerializerBase();
        Class c = ElsaSerializerBase.Header.class;
        Set<Integer> s = new TreeSet<Integer>();
        for (Field f : c.getDeclaredFields()) {
            f.setAccessible(true);
            int value = f.getInt(null);

            assertTrue("Value already used: " + value, !s.contains(value));
            s.add(value);

            if(value!= ElsaSerializerBase.Header.POJO_RESOLVER
                    && value!= ElsaSerializerBase.Header.POJO
                    && value!= ElsaSerializerBase.Header.POJO_CLASSINFO)
                assertNotNull("deser does not contain value: "+value + " - "+f.getName(), b.headerDeser[value]);

        }
        assertTrue(!s.isEmpty());
    }


    Long one = 10000L;
    Long two = 20000L;

    @Test public void object_stack_array() throws IOException {
        Object[] c = new Object[4];
        c[0]=c;
        c[1]=one;
        c[2]=two;
        c[3]=one;
        c = clone(c);
        assertTrue(c==c[0]);
        assertEquals(one, c[1]);
        assertEquals(two, c[2]);
        assertEquals(one, c[3]);
        assertTrue(c[1]==c[3]);
    }

    @Test public void object_stack_list() throws IOException {
        for(List c : Arrays.asList(new ArrayList(), new LinkedList())){
            c.add(c);
            c.add(one);
            c.add(two);
            c.add(one);
            c = clone(c);
            assertTrue(c==c.get(0));
            assertEquals(one, c.get(1));
            assertEquals(two, c.get(2));
            assertEquals(one, c.get(3));
            assertTrue(c.get(1)==c.get(3));
        }
    }

    @Test public void object_stack_set() throws IOException {
        for(Set c : Arrays.asList(new HashSet(), new LinkedHashSet())){
            c.add(c);
            c = clone(c);
            assertTrue(c.iterator().next()==c);
        }
    }


    @Test public void object_stack_map() throws IOException {
        for(Map c : Arrays.asList(new HashMap(), new LinkedHashMap(), new TreeMap(), new Properties())){
            c.put(one, c);
            c.put(two,one);
            c = clone(c);
            assertTrue(c.get(one)==c);
            assertEquals(one,c.get(two));
            Iterator i = c.keySet().iterator();
            Object one_ = i.next();
            if(one_!=c.get(two))
                one_ = i.next();
            assertTrue(one_==c.get(two));
        }
    }

    static final Object singleton = new Object();

    @Test public void singletons() throws IOException {
        ElsaSerializerPojo s = new ElsaMaker().singletons(singleton).make();
        assertTrue(singleton == clonePojo(singleton, s));
    }
}