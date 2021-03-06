package activitystarter.compiler.helpers

import activitystarter.compiler.utils.getElementType
import com.google.common.truth.Truth
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourceSubjectFactory
import com.google.testing.compile.JavaSourcesSubject
import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror
import javax.tools.JavaFileObject

object TypeMirrors {
    val String by lazy { getTypeMirror<String>() }
    val Int by lazy { getTypeMirror<Int>() }
    val Boolean by lazy { getTypeMirror<Boolean>() }
    val Char by lazy { getTypeMirror<Char>() }
    val Byte by lazy { getTypeMirror<Byte>() }
    val Short by lazy { getTypeMirror<Short>() }
    val Long by lazy { getTypeMirror<Long>() }
    val Double by lazy { getTypeMirror<Double>() }
    val Float by lazy { getTypeMirror<Float>() }
    val CharSequence by lazy { getTypeMirror("CharSequence") }
    val IntArray by lazy { getTypeMirror("int[]") }
    val BooleanArray by lazy { getTypeMirror("boolean[]") }
    val ByteArray by lazy { getTypeMirror("byte[]") }
    val ShortArray by lazy { getTypeMirror("short[]") }
    val CharArray by lazy { getTypeMirror("char[]") }
    val LongArray by lazy { getTypeMirror("long[]") }
    val FloatArray by lazy { getTypeMirror("float[]") }
    val DoubleArray by lazy { getTypeMirror("double[]") }
    val StringArray by lazy { getTypeMirror("String[]") }
    val CharSequenceArray by lazy { getTypeMirror("CharSequence[]") }
    val IntegerArrayList by lazy { getTypeMirror("ArrayList<Integer>", "java.util.ArrayList") }
    val StringArrayList by lazy { getTypeMirror("ArrayList<String>", "java.util.ArrayList") }
    val CharSequenceArrayList by lazy { getTypeMirror("ArrayList<CharSequence>", "java.util.ArrayList") }
    val SubtypeOfParcelable by lazy { getTypeMirror("Account", "android.accounts.Account") }
    val SubtypeOfSerializable by lazy { getTypeMirror("Color", "java.awt.Color") }
    val SomeEnum by lazy { getTypeMirror("Color", "java.awt.Color") }
    val ParcelableSubtypeArrayListSubtype: TypeMirror by lazy { getTypeMirror("ArrayList<Account>", "android.accounts.Account", "java.util.ArrayList") }
    val SerializableSubtypeArrayListSubtype: TypeMirror by lazy { getTypeMirror("ArrayList<Color>", "java.awt.Color", "java.util.ArrayList") }
//    ParcelableelableArrayList,

    private inline fun <reified T : Any> getTypeMirror(): TypeMirror {
        val clazz = T::class
        val name = clazz.javaPrimitiveType?.name ?: clazz.simpleName!!
        return getTypeMirror(name)
    }

    private fun getTypeMirror(typeName: String, vararg import: String): TypeMirror {
        val extraImport = import.joinToString(separator = "\n", transform = { "import $it;" })
        val source = JavaFileObjects.forSourceString("mm.Main", """
package mm;
import activitystarter.Arg;
$extraImport

public class Main {
    @Arg $typeName a;
}
        """)
        var type: TypeMirror? = null
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject, JavaSourceSubjectFactory>(JavaSourceSubjectFactory.javaSource())
                .that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(GetTypeMirrorHelperProcessor({ type = it }))!!
                .compilesWithoutError()
        return type!!
    }

    class GetTypeMirrorHelperProcessor(val typeCallback: (TypeMirror) -> Unit) : ParamProcessor() {

        override fun onParamFound(element: Element) {
            typeCallback(getElementType(element))
        }
    }
}