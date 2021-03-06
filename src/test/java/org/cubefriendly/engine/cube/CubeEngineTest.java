package org.cubefriendly.engine.cube;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Cubefriendly
 * Created by david on 24.02.15.
 */
public class CubeEngineTest {

    @Test
    public void should_be_created_by_setting_the_name_and_data(){
        DB db = DBMaker.newTempFileDB().transactionDisable().mmapFileEnableIfSupported().lockThreadUnsafeEnable().make();
        CubeDataBuilder cubeDataBuilder = CubeData.builder(db).name("new_name");
        cubeDataBuilder.add(Lists.newArrayList(1, 1, 1));
        cubeDataBuilder.add(Lists.newArrayList(2, 2, 2));
        cubeDataBuilder.add(Lists.newArrayList(1, 2, 3));
        CubeData cubeData = cubeDataBuilder.build();

        assertEquals("new_name",cubeData.name);
        assertEquals(3,cubeData.size());
    }

    @Test
    public void subset_of_data_should_be_selected(){
        DB db = DBMaker.newTempFileDB().transactionDisable().mmapFileEnableIfSupported().lockThreadUnsafeEnable().make();
        CubeDataBuilder cubeDataBuilder = CubeData.builder(db).name("new_name");
        cubeDataBuilder.add(Lists.newArrayList(2, 2, 2));
        cubeDataBuilder.add(Lists.newArrayList(3, 3, 3));
        cubeDataBuilder.add(Lists.newArrayList(1, 2, 3));
        cubeDataBuilder.add(Lists.newArrayList(1, 1, 1));
        cubeDataBuilder.add(Lists.newArrayList(1, 1, 2));

        CubeData cubeData = cubeDataBuilder.build();

        Map<Integer, List<Integer>> query = ImmutableMap.<Integer, List<Integer>>builder()
                .put(0, Lists.newArrayList(1))
                .put(1, Lists.newArrayList(1)).build();

        Iterator<int[]> it = cubeData.query(query);
        List<int[]> result = Lists.newArrayList(it);
        assertEquals(2,result.size());
        assertArrayEquals(new int[]{1,1,1},result.get(0));
        assertArrayEquals(new int[]{1,1,2},result.get(1));
        assertArrayEquals(new int[]{3,3,3},cubeData.getSizes());
    }

    @Test
    public void test_seek_with_missing_values(){
        DB db = DBMaker.newTempFileDB().transactionDisable().mmapFileEnableIfSupported().lockThreadUnsafeEnable().make();
        CubeDataBuilder cubeDataBuilder = CubeData.builder(db).name("new_name");
        cubeDataBuilder.add(Lists.newArrayList(2, 2, 2));
        cubeDataBuilder.add(Lists.newArrayList(3, 3, 3));
        cubeDataBuilder.add(Lists.newArrayList(4, 4, 4));
        cubeDataBuilder.add(Lists.newArrayList(5, 5, 5));
        cubeDataBuilder.add(Lists.newArrayList(1, 1, 1));

        CubeData cubeData = cubeDataBuilder.build();

        Map<Integer, List<Integer>> query = ImmutableMap.<Integer, List<Integer>>builder()
                .put(0, Lists.newArrayList(1,3)).build();

        Iterator<int[]> it = cubeData.query(query);
        List<int[]> result = Lists.newArrayList(it);
        assertArrayEquals(new int[]{1,1,1},result.get(0));
        assertArrayEquals(new int[]{3,3,3},result.get(1));
        assertEquals(2,result.size());
    }

    @Test
    public void failing_case_on_core(){
        DB db = DBMaker.newTempFileDB().transactionDisable().mmapFileEnableIfSupported().lockThreadUnsafeEnable().make();
        CubeDataBuilder cubeDataBuilder = CubeData.builder(db).name("new_name");
        cubeDataBuilder.add(Lists.newArrayList(1, 1, 1));
        cubeDataBuilder.add(Lists.newArrayList(2, 2, 1));
        cubeDataBuilder.add(Lists.newArrayList(2, 2, 2));
        cubeDataBuilder.add(Lists.newArrayList(1, 1, 2));

        CubeData cubeData = cubeDataBuilder.build();

        Map<Integer, List<Integer>> query = ImmutableMap.<Integer, List<Integer>>builder()
                .put(1, Lists.newArrayList(1)).build();

        Iterator<int[]> it = cubeData.query(query);
        List<int[]> result = Lists.newArrayList(it);
        assertArrayEquals(new int[]{1,1,1},result.get(0));
        assertArrayEquals(new int[]{1,1,2},result.get(1));
        assertEquals(2,result.size());

    }
}
