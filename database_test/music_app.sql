-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 24, 2025 at 03:13 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `music_app`
--

-- --------------------------------------------------------

--
-- Table structure for table `account`
--

CREATE TABLE `account` (
  `id` bigint(20) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `refresh_token` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `account`
--

INSERT INTO `account` (`id`, `email`, `enabled`, `password`, `username`, `user_id`, `refresh_token`) VALUES
(45, 'quoctienha.1509@gmail.com', b'1', '$2a$10$2qwfh8tBjchtnlGezMcTluHXw.6yETW//Hw4qrte1zEHefWFEAdw6', 'quoctien', 45, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJxdW9jdGllbiIsImlhdCI6MTc0ODA5MTczNywiZXhwIjoxNzQ4Njk2NTM3LCJ0b2tlblR5cGUiOiJyZWZyZXNoIn0.kLDt-CbAyewcUb-XfiuK6xIgiC3dbDa2m1c1oLbYB4w'),
(46, 'bkhang629@gmail.com', b'1', '$2a$10$joeTSvskS1E0z1kBV4DS..QsM5TOGtRbySrhgbaC4.YHxTCCEbgcm', 'Haha', 70, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJIYWhhIiwiaWF0IjoxNzQ3MTg4ODQ0LCJleHAiOjE3NDc3OTM2NDQsInRva2VuVHlwZSI6InJlZnJlc2gifQ.4mf7MXfpPykai9iOY2JxiPbSSgmeN9g0aFNN1muro_k');

-- --------------------------------------------------------

--
-- Table structure for table `playlist`
--

CREATE TABLE `playlist` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `customer_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `playlist`
--

INSERT INTO `playlist` (`id`, `name`, `customer_id`) VALUES
(9, 'newpl', 45);

-- --------------------------------------------------------

--
-- Table structure for table `playlist_song`
--

CREATE TABLE `playlist_song` (
  `playlist_id` bigint(20) NOT NULL,
  `song_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `playlist_song`
--

INSERT INTO `playlist_song` (`playlist_id`, `song_id`) VALUES
(9, 28);

-- --------------------------------------------------------

--
-- Table structure for table `song`
--

CREATE TABLE `song` (
  `id` bigint(20) NOT NULL,
  `dislikes` int(11) NOT NULL,
  `file_url` varchar(255) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `likes` int(11) NOT NULL,
  `lyrics` text DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `views` int(11) NOT NULL,
  `artist_id` bigint(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `license` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `song`
--

INSERT INTO `song` (`id`, `dislikes`, `file_url`, `image_url`, `likes`, `lyrics`, `title`, `views`, `artist_id`, `description`, `license`) VALUES
(2, 300, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/The+Riot_NIVIRO/NIVIRO+-+The+Riot+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/The+Riot_NIVIRO/artwork-440x440.jpg', 1000, NULL, 'The Riot', 1000000, 46, 'Song: NIVIRO - The Riot \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/TheRiot \r\nWatch: http://ncs.lnk.to/TheRiotAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(3, 300, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/TEMPO_SUPXR/SUPXR+-+TEMPO+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/TEMPO_SUPXR/artwork-440x440.jpg', 1000, NULL, 'TEMPO (Slowed) ', 20000, 47, 'Song: SUPXR - TEMPO (Slowed) \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/TEMPO\\_SL \r\nWatch: http://ncs.lnk.to/TEMPO\\_SLAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(4, 1, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Taking+It+Slow_JVNA/JVNA+-+Taking+It+Slow+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Taking+It+Slow_JVNA/artwork-440x440.jpg', 22, NULL, 'Taking It Slow [NCS Release] ', 1000000, 48, 'Song: JVNA - Taking It Slow [NCS Release] \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/TakingItSlow \r\nWatch: http://ncs.lnk.to/TakingItSlowAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(5, 33, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/So+Good_More+Plastic/More+Plastic+-+So+Good+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/So+Good_More+Plastic/artwork-440x440.jpg', 232, NULL, 'So Good ', 10000, 49, 'Song: More Plastic - So Good \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/SoGood \r\nWatch: http://ncs.lnk.to/SoGoodAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(6, 2, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Royalty+Funk_LXNGVX/LXNGVX+-+Royalty+Funk+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Royalty+Funk_LXNGVX/artwork-440x440.jpg', 2121, NULL, 'Royalty Funk', 200003, 50, 'Song: LXNGVX - Royalty Funk \r\nMusic provided by NoCopyrightSounds\r\nFree Download/Stream: http://ncs.io/royaltyfunk \r\nWatch: http://ncs.lnk.to/royaltyfunkAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(7, 2, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Real+Good+Liar_Nokae/Nokae+-+Real+Good+Liar+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Real+Good+Liar_Nokae/artwork-440x440.jpg', 22, NULL, 'Real Good Liar ', 2122121, 51, 'Song: Nokae - Real Good Liar \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: \r\nWatch:\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(8, 3, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Pushing+On_RIOT/RIOT+-+Pushing+On+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Pushing+On_RIOT/artwork-440x440.jpg', 21, NULL, 'Pushing On [NCS Release]', 123124, 52, 'Song: RIOT - Pushing On [NCS Release] \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/PushingOn \r\nWatch: http://ncs.lnk.to/PushingOnAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(9, 23, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Overdrive_Aspyer/Aspyer+-+Overdrive+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Overdrive_Aspyer/artwork-440x440.jpg', 2113, NULL, 'Overdrive [NCS Release] ', 213123, 53, 'Song: Aspyer - Overdrive [NCS Release] \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/A\\_Overdrive \r\nWatch: http://ncs.lnk.to/A\\_OverdriveAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(10, 0, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Ocean_Seanyy/Seanyy+-+Ocean+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Ocean_Seanyy/artwork-440x440.jpg', 1, NULL, 'Ocean [NCS Release]', 12312314, 54, 'Song: Seanyy - Ocean [NCS Release] \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/Ocean \r\nWatch: http://ncs.lnk.to/OceanAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(11, 0, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Obsession_More+Plastic/More+Plastic+-+Obsession+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Obsession_More+Plastic/artwork-440x440.jpg', 1, NULL, 'Obsession', 34534550, 49, 'Song: More Plastic - Obsession \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/Obsession \r\nWatch: http://ncs.lnk.to/ObsessionAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(12, 53, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/My+Heart+Is+Broken_NEYVO/NEYVO+-+My+Heart+Is+Broken+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/My+Heart+Is+Broken_NEYVO/artwork-440x440.jpg', 221, NULL, 'My Heart Is Broken [NCS Release]', 543543, 55, 'Song: NEYVO - My Heart Is Broken [NCS Release] \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/MHIB \r\nWatch: http://ncs.lnk.to/MHIBAT/youtubes\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(13, 323, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Love+Letter_m3gatron/m3gatron+-+Love+Letter+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Love+Letter_m3gatron/artwork-440x440.jpg', 3434, NULL, 'Love Letter', 4535345, 56, 'Song: m3gatron - Love Letter \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/LoveLetter \r\nWatch: http://ncs.lnk.to/LoveLetterAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(14, 0, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Live+Your+Life_Tobu/Tobu+-+Live+Your+Life+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Live+Your+Life_Tobu/artwork-440x440.jpg', 1, NULL, 'Live Your Life', 23223234, 57, 'Song: Tobu - Live Your Life \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/LiveYourLife \r\nWatch: http://ncs.lnk.to/LiveYourLifeAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(15, 32, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/I+Can+Feel_Syn+Cole/Syn+Cole+-+I+Can+Feel+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/I+Can+Feel_Syn+Cole/artwork-440x440.jpg', 324, '', 'I Can Feel', 435645, 58, 'Song: Syn Cole - I Can Feel \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/ICanFeel \r\nWatch: http://ncs.lnk.to/ICanFeelAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(16, 33, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/GET+MUCKY_Rameses+B/Rameses+B+-+GET+MUCKY+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/GET+MUCKY_Rameses+B/artwork-440x440.jpg', 2132, NULL, 'GET MUCKY [NCS Release]', 8987978, 59, 'Song: Rameses B - GET MUCKY [NCS Release] \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/GETMUCKY \r\nWatch: http://ncs.lnk.to/GETMUCKYAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(17, 22, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Funk+It_Tobu/Tobu+-+Funk+It+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Funk+It_Tobu/artwork-440x440.jpg', 324, NULL, 'Funk It [NCS Release] ', 543224, 57, 'Song: Tobu - Funk It [NCS Release] \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/FunkIt \r\nWatch: http://ncs.lnk.to/FunkItAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(18, 12, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Faster_Tobu/Tobu+-+Faster+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Faster_Tobu/artwork-440x440.jpg', 232, NULL, 'Faster [NCS Release] ', 234234, 57, 'Song: Tobu - Faster [NCS Release] \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/TFaster \r\nWatch: http://ncs.lnk.to/TFasterAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(19, 123, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/fellshortofhell_THIRST/THIRST+-+fellshortofhell++%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/fellshortofhell_THIRST/artwork-440x440.jpg', 3232, NULL, 'fellshortofhell', 3433434, 60, 'Song: THIRST - fellshortofhell \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/fellshortofhell \r\nWatch: http://ncs.lnk.to/fellshortofhellAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(20, 324, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/EXECUTIONER_DJ+FKU/DJ+FKU+-+EXECUTIONER+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/EXECUTIONER_DJ+FKU/artwork-440x440.jpg', 3221, NULL, 'EXECUTIONER [NCS Release]', 78676847, 62, 'Song: DJ FKU - EXECUTIONER [NCS Release] \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/EXECUTIONER \r\nWatch: http://ncs.lnk.to/EXECUTIONERAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(21, 2332, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Fading+Light_Fytch/Fytch+-+Fading+Light+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Fading+Light_Fytch/artwork-440x440.jpg', 23232, NULL, 'Fading Light', 45657455, 61, 'Song: Fytch - Fading Light \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/FadingLight \r\nWatch: http://ncs.lnk.to/FadingLightAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(22, 0, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Dunes_Warriyo/Warriyo+-+Dunes+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Dunes_Warriyo/artwork-440x440.jpg', 1, NULL, 'Dunes', 67567569, 63, 'Song: Warriyo - Dunes \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/Dunes \r\nWatch: http://ncs.lnk.to/DunesAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(23, 1, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Die+For+You_Whales/Whales+-+Die+For+You+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Die+For+You_Whales/artwork-440x440.jpg', 0, NULL, 'Die For You', 97885678, 64, 'Song: Whales - Die For You \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/DieForYou \r\nWatch: http://ncs.lnk.to/DieForYouAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(24, 234, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/DELTA_DJ+FKU/DJ+FKU+-+DELTA+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/DELTA_DJ+FKU/artwork-440x440.jpg', 2123, NULL, 'DELTA [NCS Release]', 75667, 62, 'Song: DJ FKU - DELTA [NCS Release] \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/DELTA \r\nWatch: http://ncs.lnk.to/DELTAAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(25, 23, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/cut+you+off_kitz/kitz+-+cut+you+off+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/cut+you+off_kitz/artwork-440x440.jpg', 3242, NULL, 'cut you off', 64353325, 65, 'Song: kitz - cut you off \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/cutyouoff \r\nWatch: http://ncs.lnk.to/cutyouoffAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(26, 1, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Crowded+Room_Josh+Rubin/Josh+Rubin+-+Crowded+Room+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Crowded+Room_Josh+Rubin/artwork-440x440.jpg', 0, NULL, 'Crowded Room', 9678624, 66, 'Song: Josh Rubin - Crowded Room \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/CrowdedRoom \r\nWatch: http://ncs.lnk.to/CrowdedRoomAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(27, 21, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/CHEAT+CODES_TOKYO+MACHINE/TOKYO+MACHINE+-+CHEAT+CODES+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/CHEAT+CODES_TOKYO+MACHINE/artwork-440x440.jpg', 3324, NULL, 'CHEAT CODES', 7657564, 67, 'Song: TOKYO MACHINE - CHEAT CODES \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/cheatcodes \r\nWatch: http://ncs.lnk.to/cheatcodesAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(28, 0, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Button+Masher_MDK/MDK+-+Button+Masher+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Button+Masher_MDK/artwork-440x440.jpg', 1, NULL, 'Button Masher ', 434354358, 68, 'Song: MDK - Button Masher \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/ButtonMasher \r\nWatch: http://ncs.lnk.to/ButtonMasherAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(29, 23, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Back+on+Dash_DJVI/DJVI+-+Back+on+Dash+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Back+on+Dash_DJVI/artwork-440x440.jpg', 2434, NULL, 'Back on Dash', 5645645, 69, 'Song: DJVI - Back on Dash \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/BackOnDash \r\nWatch: http://ncs.lnk.to/BackOnDashAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(30, 2, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Asteroid+II_Rameses+B/Rameses+B+-+Asteroid+II+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/Asteroid+II_Rameses+B/artwork-440x440.jpg', 323, NULL, 'Asteroid II [NCS Release]', 45436, 59, 'Song: Rameses B - Asteroid II [NCS Release] \r\nMusic provided by NoCopyrightSounds \r\nFree Download/Stream: http://ncs.io/AsteroidII \r\nWatch: http://ncs.lnk.to/AsteroidIIAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes'),
(31, 23, 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/AIWA_THIRST/THIRST+-+AIWA+%5BNCS+Release%5D.mp3', 'https://music-app-audio-files.s3.ap-southeast-2.amazonaws.com/Songs_NCS/AIWA_THIRST/artwork-440x440.jpg', 123, NULL, 'AIWA', 344356, 60, 'Song: THIRST - AIWA \r\nMusic provided by NoCopyrightSounds\r\nFree Download/Stream: http://ncs.io/AIWA \r\nWatch: http://ncs.lnk.to/AIWAAT/youtube\r\n', 'Music provided by NoCopyrightSounds for educational purposes');

-- --------------------------------------------------------

--
-- Table structure for table `song_rating`
--

CREATE TABLE `song_rating` (
  `id` bigint(20) NOT NULL,
  `liked` bit(1) DEFAULT NULL,
  `account_id` bigint(20) NOT NULL,
  `song_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `song_rating`
--

INSERT INTO `song_rating` (`id`, `liked`, `account_id`, `song_id`) VALUES
(2, b'0', 45, 26),
(3, b'1', 45, 10),
(4, b'1', 45, 14),
(5, b'1', 45, 11),
(6, b'1', 45, 28),
(7, b'1', 45, 22),
(8, b'0', 45, 23);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `user_type` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `membership` enum('NORMAL','PREMIUM') DEFAULT NULL,
  `stage_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`user_type`, `id`, `full_name`, `phone`, `membership`, `stage_name`) VALUES
('CUSTOMER', 45, 'Ha Quoc Tien', '9999999999', 'NORMAL', NULL),
('ARTIST', 46, NULL, NULL, NULL, 'NIVIRO'),
('ARTIST', 47, NULL, NULL, NULL, 'SUPXR'),
('ARTIST', 48, NULL, NULL, NULL, 'JVNA'),
('ARTIST', 49, NULL, NULL, NULL, 'More Plastic'),
('ARTIST', 50, NULL, NULL, NULL, 'LXNGVX'),
('ARTIST', 51, NULL, NULL, NULL, 'Nokae'),
('ARTIST', 52, NULL, NULL, NULL, 'RIOT'),
('ARTIST', 53, NULL, NULL, NULL, 'Aspyer'),
('ARTIST', 54, NULL, NULL, NULL, 'Seanyy'),
('ARTIST', 55, NULL, NULL, NULL, 'NEYVO'),
('ARTIST', 56, NULL, NULL, NULL, 'm3gatron'),
('ARTIST', 57, NULL, NULL, NULL, 'Tobu'),
('ARTIST', 58, NULL, NULL, NULL, 'Syn Cole'),
('ARTIST', 59, NULL, NULL, NULL, 'Rameses B'),
('ARTIST', 60, NULL, NULL, NULL, 'THIRST'),
('ARTIST', 61, NULL, NULL, NULL, 'Fytch'),
('ARTIST', 62, NULL, NULL, NULL, 'DJ FKU'),
('ARTIST', 63, NULL, NULL, NULL, 'Warriyo'),
('ARTIST', 64, NULL, NULL, NULL, 'Whales'),
('ARTIST', 65, NULL, NULL, NULL, 'kitz'),
('ARTIST', 66, NULL, NULL, NULL, 'Josh Rubin'),
('ARTIST', 67, NULL, NULL, NULL, 'TOKYO MACHINE'),
('ARTIST', 68, NULL, NULL, NULL, 'MDK'),
('ARTIST', 69, NULL, NULL, NULL, 'DJVI'),
('CUSTOMER', 70, 'Kang', '1234567898', 'NORMAL', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `verification_token`
--

CREATE TABLE `verification_token` (
  `id` bigint(20) NOT NULL,
  `expiry_date` datetime(6) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `account_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci ROW_FORMAT=DYNAMIC;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `account`
--
ALTER TABLE `account`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKh6dr47em6vg85yuwt4e2roca4` (`user_id`);

--
-- Indexes for table `playlist`
--
ALTER TABLE `playlist`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKknq37hyeydhnd3c2ww3i7mmdi` (`customer_id`);

--
-- Indexes for table `playlist_song`
--
ALTER TABLE `playlist_song`
  ADD KEY `FK8l4jevlmxwsdm3ppymxm56gh2` (`song_id`),
  ADD KEY `FKji5gt6i2hcwyt9x1fcfndclva` (`playlist_id`);

--
-- Indexes for table `song`
--
ALTER TABLE `song`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK7oarm0rg2cmcostoym9tovqtk` (`artist_id`);

--
-- Indexes for table `song_rating`
--
ALTER TABLE `song_rating`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK22c18i1fke3dejgkdg6nb8b7x` (`account_id`),
  ADD KEY `FKl3kwwyc9bmy8889uok85nokmh` (`song_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `verification_token`
--
ALTER TABLE `verification_token`
  ADD PRIMARY KEY (`id`) USING BTREE,
  ADD UNIQUE KEY `UKhcdftkkphim5iwk2f9ffmm8bt` (`account_id`) USING BTREE;

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `account`
--
ALTER TABLE `account`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=47;

--
-- AUTO_INCREMENT for table `playlist`
--
ALTER TABLE `playlist`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `song`
--
ALTER TABLE `song`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT for table `song_rating`
--
ALTER TABLE `song_rating`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=71;

--
-- AUTO_INCREMENT for table `verification_token`
--
ALTER TABLE `verification_token`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `account`
--
ALTER TABLE `account`
  ADD CONSTRAINT `FK7m8ru44m93ukyb61dfxw0apf6` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Constraints for table `playlist`
--
ALTER TABLE `playlist`
  ADD CONSTRAINT `FKknq37hyeydhnd3c2ww3i7mmdi` FOREIGN KEY (`customer_id`) REFERENCES `user` (`id`);

--
-- Constraints for table `playlist_song`
--
ALTER TABLE `playlist_song`
  ADD CONSTRAINT `FK8l4jevlmxwsdm3ppymxm56gh2` FOREIGN KEY (`song_id`) REFERENCES `song` (`id`),
  ADD CONSTRAINT `FKji5gt6i2hcwyt9x1fcfndclva` FOREIGN KEY (`playlist_id`) REFERENCES `playlist` (`id`);

--
-- Constraints for table `song`
--
ALTER TABLE `song`
  ADD CONSTRAINT `FK7oarm0rg2cmcostoym9tovqtk` FOREIGN KEY (`artist_id`) REFERENCES `user` (`id`);

--
-- Constraints for table `song_rating`
--
ALTER TABLE `song_rating`
  ADD CONSTRAINT `FK22c18i1fke3dejgkdg6nb8b7x` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`),
  ADD CONSTRAINT `FKl3kwwyc9bmy8889uok85nokmh` FOREIGN KEY (`song_id`) REFERENCES `song` (`id`);

--
-- Constraints for table `verification_token`
--
ALTER TABLE `verification_token`
  ADD CONSTRAINT `FKs8je6hs7qhfs93obh8dqml9fe` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
