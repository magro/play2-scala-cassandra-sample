-- description: creates songs and playlists tables
-- authoredAt: 201403202239
-- up:

CREATE TABLE playlists (
  id uuid,
  title text,
  album text, 
  artist text,
  song_id uuid,
  PRIMARY KEY (id, title, album, artist)
)

-- down:

DROP TABLE playlists