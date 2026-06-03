Place the title-screen track here as:

  main_theme.ogg

Minecraft does not load MP3 files. Convert your audio first, for example:

  ffmpeg -i "main_theme.mp3" -c:a libvorbis -q:a 4 main_theme.ogg

The file name must match sounds.json (music/menu/main_theme).
