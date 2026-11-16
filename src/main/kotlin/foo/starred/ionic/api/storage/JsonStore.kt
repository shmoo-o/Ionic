package foo.starred.ionic.api.storage

import foo.starred.ionic.Ionic
import foo.starred.kontext.api.storage.impl.generic.impl.GenericJsonStore

class JsonStore(path: String, tts: Int = 15) : GenericJsonStore(Ionic.Data.id, path, tts)
