package org.ebfhub.fastprotobuf;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>MutableByteArrayInputStream class.</p>
 *
 * @author mac
 * @version $Id: $Id
 */
public class MutableByteArrayInputStream extends InputStream
{
    byte[] buf;
    int len;
    int pos;

    /** {@inheritDoc} */
    @Override
    public int read() {
        if(pos==len){
            return -1;
        }
        return buf[pos++];
    }

    /**
     * <p>setBytes.</p>
     *
     * @param tmp an array of {@link byte} objects.
     * @param tmpLen a int.
     */
    public void setBytes(byte[] tmp, int tmpLen) {
        buf=tmp;
        len=tmpLen;
        pos=0;
    }
}
