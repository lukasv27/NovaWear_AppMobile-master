import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectonovawear.api.ChatApiService
import com.example.proyectonovawear.model.Chat
import com.example.proyectonovawear.model.Mensaje
import com.example.proyectonovawear.network.RetrofitProvider
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val api: ChatApiService = RetrofitProvider.create()
    private val _chats = mutableStateListOf<Chat>()
    val chats: List<Chat> get() = _chats

    fun getChatForProduct(productoId: Long, productoNombre: String): Chat {
        val existing = _chats.find { it.productoId == productoId }
        if (existing != null) return existing

        val nuevo = Chat(productoId = productoId, productoNombre = productoNombre)
        _chats.add(nuevo)

        viewModelScope.launch {
            try {
                val chatData = api.getChatByProducto(productoId)
                val mensajesConEsMio = chatData.mensajes.map { mensaje ->
                    mensaje.copy(esMio = mensaje.personaId == personaId)
                }

                nuevo.mensajes.clear()
                nuevo.mensajes.addAll(mensajesConEsMio)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return nuevo
    }


    private val personaId: Long = 1L  // aquí puedes cargarlo desde SharedPreferences o sesión

    fun addMessage(productoId: Long, productoNombre: String, mensaje: Mensaje) {
        val chat = getChatForProduct(productoId, productoNombre)
        chat.mensajes.add(mensaje.copy(esMio = true))



        viewModelScope.launch {
            try {
                api.enviarMensaje(productoId, personaId, mensaje.contenido)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
