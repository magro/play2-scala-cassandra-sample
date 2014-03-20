-- description: creates songs and playlists tables
-- authoredAt: 201403202239
-- up:

CREATE TABLE songs (
  id uuid PRIMARY KEY,
  title text,
  album text,
  artist text,
  tags set<text>,
  data blob
)

-- down:

DROP TABLE songs