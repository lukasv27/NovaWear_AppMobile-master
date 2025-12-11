import android.net.Uri
import com.example.proyectonovawear.api.ApiService
import com.example.proyectonovawear.controller.CrearProductosViewModel
import com.example.proyectonovawear.model.Productos
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class CrearProductosViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var apiService: ApiService
    private lateinit var viewModel: CrearProductosViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        apiService = mock()
        // 👇 evitar que se cargue automáticamente en el init
        viewModel = CrearProductosViewModel(apiService, autoLoad = false)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargarProductos devuelve lista correctamente`() = runTest {
        val listaFake = listOf(
            Productos(nombre = "polera", descripcion = "roja", talla = "M", precio = 12000),
            Productos(nombre = "pantalón", descripcion = "azul", talla = "L", precio = 22000)
        )

        whenever(apiService.getProductos()).thenReturn(listaFake)

        viewModel.cargarProductos()

        // 👇 avanzar corrutinas para que se complete la carga
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assert(!state.listaCargando)
        assert(state.list.size == 2)
        assert(state.list[0].nombre == "polera")
        assert(state.list[1].nombre == "pantalón")
    }

    @Test
    fun `agregarProducto debe mostrar error si hay campos vacios`() = runTest {
        viewModel.agregarProducto()

        testDispatcher.scheduler.advanceUntilIdle()

        val result = viewModel.state.value
        assert(result.createError == "Todos los campos son obligatorios")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `agregarProducto añade producto a la lista`() = runTest {
        val mockApi = mockk<ApiService>()
        val productoMock = Productos(
            nombre = "Polera negra",
            descripcion = "Cuello redondo",
            talla = "M",
            precio = 12000,
            imagenUri = "uri://mock"
        )

        // Mockear la llamada
        coEvery { mockApi.agregarProducto(any(), any()) } returns productoMock

        val viewModel = CrearProductosViewModel(api = mockApi)
        viewModel.setPersonaId(1L)

        viewModel.onNombreChange("Polera negra")
        viewModel.onDescripcionChange("Cuello redondo")
        viewModel.onTallaChange("M")
        viewModel.onPrecioChange(12000)
        viewModel.onImageChange(mockk<Uri>())

        viewModel.agregarProducto()

        // Avanzar coroutines
        advanceUntilIdle()

        val result = viewModel.state.value
        assert(result.list.contains(productoMock))
        assert(result.created == true)
    }

}