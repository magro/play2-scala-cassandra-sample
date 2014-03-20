-- description: populates songs and playlists
-- authoredAt: 201403202240
-- up:
          
INSERT INTO playlists (id, song_id, title, album, artist) 
  VALUES (
    2cc9ccb7-6221-4ccb-8387-f22b6a1b354d,
    756716f7-2e54-4715-9f00-91dcbea6cf50,
    'La Petite Tonkinoise',
    'Bye Bye Blackbird',
    'Joséphine Baker'
  )

-- down:

TRUNCATE playlists