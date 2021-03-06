/*
 * Copyright (c) 2018-2020 "Graph Foundation"
 * Graph Foundation, Inc. [https://graphfoundation.org]
 *
 * Copyright (c) 2002-2020 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of ONgDB.
 *
 * ONgDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package files;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import org.neo4j.internal.kernel.api.schema.IndexProviderDescriptor;
import org.neo4j.kernel.api.impl.schema.LuceneIndexProviderFactory;
import org.neo4j.kernel.impl.index.schema.NumberIndexProvider;
import org.neo4j.kernel.impl.index.schema.SpatialIndexProvider;
import org.neo4j.kernel.impl.index.schema.StringIndexProvider;
import org.neo4j.kernel.impl.index.schema.TemporalIndexProvider;
import org.neo4j.kernel.internal.NativeIndexFileFilter;
import org.neo4j.test.rule.TestDirectory;
import org.neo4j.test.rule.fs.DefaultFileSystemRule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.neo4j.kernel.api.impl.schema.NativeLuceneFusionIndexProviderFactory.subProviderDirectoryStructure;
import static org.neo4j.kernel.api.index.IndexDirectoryStructure.directoriesByProviderKey;

public class NativeIndexFileFilterTest
{
    private static final IndexProviderDescriptor LUCENE_DESCRTIPTOR = LuceneIndexProviderFactory.PROVIDER_DESCRIPTOR;

    @Rule
    public DefaultFileSystemRule fs = new DefaultFileSystemRule();
    @Rule
    public TestDirectory directory = TestDirectory.testDirectory();

    private File storeDir;
    private NativeIndexFileFilter filter;

    @Before
    public void before()
    {
        storeDir = directory.directory();
        filter = new NativeIndexFileFilter( storeDir );
    }

    @Test
    public void shouldNotAcceptFileFromPureLuceneProvider() throws IOException
    {
        // given
        File dir = directoriesByProviderKey( storeDir ).forProvider( LUCENE_DESCRTIPTOR ).directoryForIndex( 1 );
        File file = new File( dir, "some-file" );
        createFile( file );

        // when
        boolean accepted = filter.accept( file );

        // then
        assertFalse( accepted );
    }

    @Test
    public void shouldNotAcceptLuceneFileFromFusionProvider() throws IOException
    {
        // given
        File dir = subProviderDirectoryStructure( storeDir, LUCENE_DESCRTIPTOR ).forProvider( LUCENE_DESCRTIPTOR ).directoryForIndex( 1 );
        File file = new File( dir, "some-file" );
        createFile( file );

        // when
        boolean accepted = filter.accept( file );

        // then
        assertFalse( accepted );
    }

    @Test
    public void shouldAcceptNativeStringIndexFileFromFusionProvider() throws IOException
    {
        shouldAcceptNativeIndexFileFromFusionProvider( new IndexProviderDescriptor( StringIndexProvider.KEY, "some-version" ) );
    }

    @Test
    public void shouldAcceptNativeNumberIndexFileFromFusionProvider() throws IOException
    {
        shouldAcceptNativeIndexFileFromFusionProvider( new IndexProviderDescriptor( NumberIndexProvider.KEY, "some-version" ) );
    }

    @Test
    public void shouldAcceptNativeSpatialIndexFileFromFusionProvider() throws IOException
    {
        shouldAcceptNativeIndexFileFromFusionProvider( new IndexProviderDescriptor( SpatialIndexProvider.KEY, "some-version" ) );
    }

    @Test
    public void shouldAcceptNativeTemporalIndexFileFromFusionProvider() throws IOException
    {
        shouldAcceptNativeIndexFileFromFusionProvider( new IndexProviderDescriptor( TemporalIndexProvider.KEY, "some-version" ) );
    }

    private void shouldAcceptNativeIndexFileFromFusionProvider( IndexProviderDescriptor descriptor ) throws IOException
    {
        // given
        File dir = subProviderDirectoryStructure( storeDir, descriptor ).forProvider( descriptor ).directoryForIndex( 1 );
        File file = new File( dir, "some-file" );
        createFile( file );

        // when
        boolean accepted = filter.accept( file );

        // then
        assertTrue( accepted );
    }

    private void createFile( File file ) throws IOException
    {
        fs.mkdirs( file.getParentFile() );
        fs.create( file ).close();
    }
}
