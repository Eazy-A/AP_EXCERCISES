/**
* FINAL EXAM TASK - MOVIE STREAMING PLATFORM
* Time: 120 minutes
* Points: 100
*
* TASK DESCRIPTION:
* Create a movie streaming platform system that manages users, movies, and watch history.
*
* REQUIREMENTS:
*
* 1. USER TYPES (15 points):
*    - FreeUser: can watch movies up to 720p, max 5 movies per month
*    - PremiumUser: can watch movies up to 4K, unlimited movies
*    - Both have watchHistory (list of Movie objects they watched)
*
* 2. MOVIE CLASS (10 points):
*    - Properties: id, title, genre, duration (minutes), releaseYear, rating (1-10)
*    - Must be comparable by rating (descending), then title (ascending)
*
* 3. WATCH EVENT (15 points):
*    - Track when user watches a movie: userId, movieId, timestamp, quality (480p/720p/1080p/4K)
*    - Validate that user can watch at requested quality
*    - FreeUser: throw UserPrivilegeException if trying to watch >720p or >5 movies this month
*    - Track watch count per user per month
*
* 4. STREAMING PLATFORM CLASS (60 points):
*    - readMovies(InputStream): read format "id;title;genre;duration;year;rating"
*    - readUsers(InputStream): read format "F;id;email;name" or "P;id;email;name;subscriptionDate"
*    - watchMovie(String userId, String movieId, String quality):
*        * Validate user exists and can watch
*        * Record watch event with current timestamp
*        * Add movie to user's watch history
*        * Return success/failure
*
*    - Map<String, List<Movie>> getMoviesByGenre(int minRating):
*        * Group movies by genre that have rating >= minRating
*        * Sort movies within each genre by rating (desc), then title (asc)
*        * Sort genres alphabetically
*
*    - Map<Integer, Long> getWatchStatsByYear():
*        * Count total watches per release year
*        * Return map sorted by year (descending)
*
*    - List<User> getTopNUsers(int n):
*        * Return top N users by number of unique movies watched
*        * If tied, sort by userId alphabetically
*
*    - Map<String, Double> getAverageRatingByGenre():
*        * Calculate average rating of all watched movies per genre
*        * Only include genres that have been watched
*        * Sort by average rating (descending)
*
*    - void generateReport(OutputStream os):
*        * Print detailed platform statistics
*
* BONUS (10 points):
*    - Implement binge-watching detection: flag users who watched 5+ movies in same day
*    - getPremiumConversionCandidates(): FreeUsers who hit their monthly limit 2+ times
*
* EVALUATION CRITERIA:
* - Correct OOP design (inheritance, polymorphism)
* - Proper use of Collections and Stream API
* - Exception handling
* - Code organization and readability
* - Edge case handling
    *//**
* FINAL EXAM TASK - MOVIE STREAMING PLATFORM
* Time: 120 minutes
* Points: 100
*
* TASK DESCRIPTION:
* Create a movie streaming platform system that manages users, movies, and watch history.
*
* REQUIREMENTS:
*
* 1. USER TYPES (15 points):
*    - FreeUser: can watch movies up to 720p, max 5 movies per month
*    - PremiumUser: can watch movies up to 4K, unlimited movies
*    - Both have watchHistory (list of Movie objects they watched)
*
* 2. MOVIE CLASS (10 points):
*    - Properties: id, title, genre, duration (minutes), releaseYear, rating (1-10)
*    - Must be comparable by rating (descending), then title (ascending)
*
* 3. WATCH EVENT (15 points):
*    - Track when user watches a movie: userId, movieId, timestamp, quality (480p/720p/1080p/4K)
*    - Validate that user can watch at requested quality
*    - FreeUser: throw UserPrivilegeException if trying to watch >720p or >5 movies this month
*    - Track watch count per user per month
*
* 4. STREAMING PLATFORM CLASS (60 points):
*    - readMovies(InputStream): read format "id;title;genre;duration;year;rating"
*    - readUsers(InputStream): read format "F;id;email;name" or "P;id;email;name;subscriptionDate"
*    - watchMovie(String userId, String movieId, String quality):
*        * Validate user exists and can watch
*        * Record watch event with current timestamp
*        * Add movie to user's watch history
*        * Return success/failure
*
*    - Map<String, List<Movie>> getMoviesByGenre(int minRating):
*        * Group movies by genre that have rating >= minRating
*        * Sort movies within each genre by rating (desc), then title (asc)
*        * Sort genres alphabetically
*
*    - Map<Integer, Long> getWatchStatsByYear():
*        * Count total watches per release year
*        * Return map sorted by year (descending)
*
*    - List<User> getTopNUsers(int n):
*        * Return top N users by number of unique movies watched
*        * If tied, sort by userId alphabetically
*
*    - Map<String, Double> getAverageRatingByGenre():
*        * Calculate average rating of all watched movies per genre
*        * Only include genres that have been watched
*        * Sort by average rating (descending)
*
*    - void generateReport(OutputStream os):
*        * Print detailed platform statistics
*
* BONUS (10 points):
*    - Implement binge-watching detection: flag users who watched 5+ movies in same day
*    - getPremiumConversionCandidates(): FreeUsers who hit their monthly limit 2+ times
*
* EVALUATION CRITERIA:
* - Correct OOP design (inheritance, polymorphism)
* - Proper use of Collections and Stream API
* - Exception handling
* - Code organization and readability
* - Edge case handling
    */