package it.unipd.dei.pseaiproject

//classe di enumerazione per dare un nome agli stili presenti: dark e light
enum class ThemeType(val themeResId: Int) {
    DARK(R.style.DarkStyle),
    LIGHT(R.style.LightStyle)
}