package org.emfjson.cdo.sample;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.ecore.EObject;
import org.emfjson.common.Options;
import org.emfjson.jackson.JacksonOptions;
import org.emfjson.jackson.databind.ser.IdSerializer;
import org.emfjson.jackson.databind.ser.ResourceSerializer;
import org.emfjson.jackson.module.EMFModule;
import org.emfjson.model.ModelFactory;
import org.emfjson.model.ModelPackage;
import org.emfjson.model.User;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws CommitException {
        CDOServer server = new CDOServer("sample");
        server.start();

        CDOSessionConfiguration configuration = server.getSessionConfiguration();
        CDOSession session = configuration.openSession();
        session.getPackageRegistry().putEPackage(ModelPackage.eINSTANCE);

        CDOTransaction transaction = session.openTransaction();
        CDOResource r1 = transaction.getOrCreateResource("u1");
        r1.getContents().clear();

        User u1 = ModelFactory.eINSTANCE.createUser();
        u1.setName("Bob");
        r1.getContents().add(u1);

        CDOResource r2 = transaction.getOrCreateResource("u2");
        r2.getContents().clear();

        User u2 = ModelFactory.eINSTANCE.createUser();
        u2.setName("Peter");
        u2.getFriends().add(u1);
        r2.getContents().add(u2);

        transaction.commit();

        ObjectMapper mapper = new ObjectMapper();
        JacksonOptions options = new JacksonOptions.Builder()
                .withID(true)
                .withIdSerializer(new IdSerializer() {
                    @Override
                    public void serialize(EObject object, JsonGenerator jg, Options options) throws IOException {
                        final CDOObject cdoObject = CDOUtil.getCDOObject(object);
                        final CDOID cdoid = CDOIDUtil.getCDOID(cdoObject);

                        jg.writeNumberField(options.idField, CDOIDUtil.getLong(cdoid));
                    }}
                )
                .withReferenceSerializer(new CDOReferenceSerializer())
                .build();

        EMFModule module = new EMFModule(transaction.getResourceSet(), options);
        module.addSerializer(CDOResource.class, new ResourceSerializer());
        mapper.registerModule(module);

        System.out.println(mapper.valueToTree(r1));
        System.out.println(mapper.valueToTree(r2));

        transaction.close();
        session.close();

        server.stop();
    }

}
