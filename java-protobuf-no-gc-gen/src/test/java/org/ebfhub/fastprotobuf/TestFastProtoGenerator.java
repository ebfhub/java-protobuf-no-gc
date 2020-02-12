package org.ebfhub.fastprotobuf;

import com.salesforce.jprotoc.ProtocPlugin;
import org.junit.Test;

public class TestFastProtoGenerator {
    @Test
    public void testGenerate()
    {
        FastProtoGenerator gen=new FastProtoGenerator(true);
        ProtocPlugin.debug(gen,"src/test/resources/descriptor_dump");

    }
}
