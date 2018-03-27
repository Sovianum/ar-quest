package technopark.diploma.arquest.core.ar.geom;

import com.google.ar.core.Pose;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class GeomTest {

    @Test
    public void testApplyTranslation() throws Exception {
        Geom transGeom = new Geom();
        float[] trans = new float[3];

        transGeom.getPose().getTranslation(trans, 0);
        compareElementwise(new Float[]{0f, 0f, 0f}, box(trans));

        transGeom.apply(Pose.makeTranslation(1, 2, 3));
        transGeom.getPose().getTranslation(trans, 0);
        compareElementwise(new Float[]{1f, 2f, 3f}, box(trans));

        transGeom.apply(Pose.makeTranslation(1, 2, 3));
        transGeom.getPose().getTranslation(trans, 0);
        compareElementwise(new Float[]{2f, 4f, 6f}, box(trans));
    }

    @Test
    public void testApplyRotation() throws Exception {
        Geom transGeom = new Geom();
        float[] q = new float[4];
        float[] eq = new float[4];

        transGeom.getPose().getRotationQuaternion(q, 0);
        compareElementwise(new Float[]{0f, 0f, 0f, 1f}, box(q));

        float halfSqrt2 = (float) Math.sqrt(2) / 2;
        Pose rotation1 = Pose.makeRotation(halfSqrt2, 0, 0, halfSqrt2);
        Pose rotation2 = Pose.makeRotation(0, halfSqrt2, 0, halfSqrt2);

        transGeom.apply(rotation1);
        transGeom.getPose().getRotationQuaternion(q, 0);
        rotation1.getRotationQuaternion(eq, 0);
        compareElementwise(box(eq), box(q));

        transGeom.apply(rotation2);
        transGeom.getPose().getRotationQuaternion(q, 0);
        rotation2.compose(rotation1).getRotationQuaternion(eq, 0);
        compareElementwise(box(eq), box(q));
    }

    @Test
    public void testApplyBoth() throws Exception {
        Geom transGeom = new Geom();
        float[] t = new float[3];
        float[] q = new float[4];
        float[] eq = new float[4];

        float halfSqrt2 = (float) Math.sqrt(2) / 2;
        Pose rotation = Pose.makeRotation(halfSqrt2, 0, 0, halfSqrt2);
        Pose translation = Pose.makeTranslation(1, 1, 1);

        transGeom.apply(translation).apply(rotation).apply(translation);
        transGeom.getPose().getRotationQuaternion(q, 0);
        rotation.getRotationQuaternion(eq, 0);
        compareElementwise(box(eq), box(q));

        transGeom.getPose().getTranslation(t, 0);
        compareElementwise(new Float[]{2f, 2f, 2f}, box(t));
    }

    private static <T> void compareElementwise(T[] a, T[] b) {
        assertEquals(a.length, b.length);
        for (int i = 0; i != a.length; ++i) {
            assertEquals(String.valueOf(i), a[i], b[i]);
        }
    }

    private static Float[] box(float[] floats) {
        Float[] res = new Float[floats.length];
        for (int i = 0; i != floats.length; ++i) {
            res[i] = floats[i];
        }
        return res;
    }
}