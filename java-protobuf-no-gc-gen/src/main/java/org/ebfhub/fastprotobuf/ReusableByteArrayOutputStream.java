package org.ebfhub.fastprotobuf;

import java.io.ByteArrayOutputStream;

/**
 * <p>ReusableByteArrayOutputStream class.</p>
 *
 * @author mac
 * @version $Id: $Id
 */
public class ReusableByteArrayOutputStream extends ByteArrayOutputStream
{
    /**
     * <p>getBytes.</p>
     *
     * @return an array of {@link byte} objects.
     */
    public byte[] getBytes(){
        return buf;
    }

}
