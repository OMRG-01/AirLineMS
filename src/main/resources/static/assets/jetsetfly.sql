-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 08, 2024 at 09:30 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `jetsetfly`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin_r`
--

CREATE TABLE `admin_r` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `image` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin_r`
--

INSERT INTO `admin_r` (`id`, `name`, `email`, `password`, `image`) VALUES
(1, 'GANESH MALI', 'ganarm2003@gmail.com', '$2y$10$deBJ9ShByucrpbHGqrnSROxFceK.CsYR80clFVsj375YcR6V0CPC2', ''),
(2, 'SANAT PILLAI', 'sanatrock2003@gmail.com', '$2y$10$2U7aFYtWYNN./e3zMVv/OO2wHx6cgUtPlWlnQeMfuO82FPRk82lwy', '');

-- --------------------------------------------------------

--
-- Table structure for table `airline`
--

CREATE TABLE `airline` (
  `ano` int(255) NOT NULL,
  `aname` varchar(128) NOT NULL,
  `added_date` varchar(128) NOT NULL DEFAULT current_timestamp(),
  `no_of_flights` int(255) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `airline`
--

INSERT INTO `airline` (`ano`, `aname`, `added_date`, `no_of_flights`) VALUES
(1, 'Emirates', '07-04-2024', 0),
(2, 'IndiGo', '07-04-2024', 0),
(3, 'AirAsia', '07-04-2024', 0),
(4, 'AirIndia', '07-04-2024', 0);

-- --------------------------------------------------------

--
-- Table structure for table `booking`
--

CREATE TABLE `booking` (
  `booking_id` int(11) NOT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `booking`
--

INSERT INTO `booking` (`booking_id`, `id`) VALUES
(1, 1),
(6, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(7, 5),
(8, 6),
(9, 6);

-- --------------------------------------------------------

--
-- Table structure for table `class`
--

CREATE TABLE `class` (
  `class_id` int(255) NOT NULL,
  `cname` varchar(20) NOT NULL,
  `noofseats` int(255) NOT NULL DEFAULT 0,
  `fno` int(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `class`
--

INSERT INTO `class` (`class_id`, `cname`, `noofseats`, `fno`) VALUES
(1, 'Economy Class', 22, 101),
(2, 'Business Class', 18, 101),
(3, 'First Class', 20, 101),
(4, 'Economy Class', 40, 102),
(5, 'Business Class', 30, 102),
(6, 'First Class', 35, 102),
(7, 'Economy Class', 20, 201),
(8, 'Business Class', 10, 201),
(9, 'First Class', 15, 201),
(10, 'Economy Class', 25, 202),
(11, 'Business Class', 0, 202),
(12, 'First Class', 25, 202),
(13, 'Economy Class', 40, 401),
(14, 'Business Class', 15, 401),
(15, 'First Class', 0, 401),
(16, 'Economy Class', 25, 301),
(17, 'Business Class', 13, 301),
(18, 'First Class', 17, 301);

-- --------------------------------------------------------

--
-- Table structure for table `feedback`
--

CREATE TABLE `feedback` (
  `seq_no` int(255) NOT NULL,
  `firstimpression` varchar(255) NOT NULL,
  `come` varchar(255) NOT NULL,
  `missing` varchar(255) NOT NULL,
  `rating` int(10) NOT NULL,
  `id` int(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `feedback`
--

INSERT INTO `feedback` (`seq_no`, `firstimpression`, `come`, `missing`, `rating`, `id`) VALUES
(1, 'Amazing Website, Nice User Interface', 'Friends/Relatives', 'Some Pages Not contain footer', 4, 3),
(2, 'fantastic work, superb booking, love the flight animation', 'Social Media', 'Return Ticket Not Available', 5, 2),
(3, 'unsatisfactory reservation, faulty payment  ', 'Television', 'When booking is completed no any mail is received', 2, 1),
(4, 'nice website', 'Search Engine', 'Nothing', 3, 1);

-- --------------------------------------------------------

--
-- Table structure for table `flight`
--

CREATE TABLE `flight` (
  `seq_no` int(255) NOT NULL,
  `fno` int(255) NOT NULL,
  `tnoseats` int(255) NOT NULL,
  `ano` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `flight`
--

INSERT INTO `flight` (`seq_no`, `fno`, `tnoseats`, `ano`) VALUES
(1, 101, 60, 1),
(2, 102, 105, 1),
(3, 201, 45, 2),
(4, 202, 50, 2),
(5, 401, 55, 4),
(6, 301, 55, 3);

-- --------------------------------------------------------

--
-- Table structure for table `flight_availabality`
--

CREATE TABLE `flight_availabality` (
  `s` int(11) NOT NULL,
  `seatsofeco` int(11) NOT NULL DEFAULT 0,
  `seatsofbsn` int(11) NOT NULL DEFAULT 0,
  `seatsoffrt` int(11) NOT NULL DEFAULT 0,
  `seq_no` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `flight_availabality`
--

INSERT INTO `flight_availabality` (`s`, `seatsofeco`, `seatsofbsn`, `seatsoffrt`, `seq_no`) VALUES
(1, 40, 30, 33, 1),
(2, 40, 27, 35, 2),
(3, 40, 13, 0, 3),
(4, 25, 0, 23, 4),
(5, 20, 9, 15, 5),
(6, 25, 0, 25, 6),
(7, 35, 15, 0, 7),
(8, 25, 13, 17, 8),
(9, 20, 10, 15, 9),
(10, 40, 12, 0, 10),
(11, 25, 13, 17, 11),
(12, 25, 0, 21, 12),
(13, 38, 15, 0, 13),
(14, 22, 18, 20, 14),
(15, 25, 13, 17, 15);

-- --------------------------------------------------------

--
-- Table structure for table `flight_schedule`
--

CREATE TABLE `flight_schedule` (
  `seq_no` int(255) NOT NULL,
  `source` varchar(255) NOT NULL,
  `destination` varchar(255) NOT NULL,
  `adatetime` datetime NOT NULL,
  `ddatetime` datetime NOT NULL,
  `fno` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `flight_schedule`
--

INSERT INTO `flight_schedule` (`seq_no`, `source`, `destination`, `adatetime`, `ddatetime`, `fno`) VALUES
(1, 'BOM', 'HYD', '2024-04-08 12:10:00', '2024-04-08 14:20:00', 102),
(2, 'HYD', 'CCU', '2024-04-08 23:30:00', '2024-04-09 03:30:00', 102),
(3, 'ATQ', 'KOC', '2024-04-09 14:15:00', '2024-04-09 18:30:00', 401),
(4, 'ATQ', 'KOC', '2024-04-09 07:30:00', '2024-04-09 10:00:00', 202),
(5, 'BGL', 'AMD', '2024-04-10 04:37:00', '2024-04-10 07:38:00', 201),
(6, 'NSK', 'BOM', '2024-04-07 17:25:00', '2024-04-07 19:25:00', 202),
(7, 'BOM', 'CCU', '2024-04-11 18:43:00', '2024-04-11 21:30:00', 401),
(8, 'NSK', 'BOM', '2024-04-08 04:45:00', '2024-04-08 05:45:00', 301),
(9, 'NSK', 'BOM', '2024-04-08 10:15:00', '2024-04-08 11:50:00', 201),
(10, 'NSK', 'BOM', '2024-04-08 15:00:00', '2024-04-08 17:50:00', 401),
(11, 'BGL', 'AMD', '2024-04-12 15:00:00', '2024-04-12 17:15:00', 301),
(12, 'DEL', 'HYD', '2024-04-13 16:00:00', '2024-04-13 18:05:00', 202),
(13, 'MAA', 'ATQ', '2024-04-14 11:00:00', '2024-04-14 13:05:00', 401),
(14, 'ATQ', 'KOC', '2024-04-09 02:32:00', '2024-04-09 04:32:00', 101),
(15, 'CCU', 'NSK', '2024-04-15 11:50:00', '2024-04-15 17:48:00', 301);

-- --------------------------------------------------------

--
-- Table structure for table `otp_table`
--

CREATE TABLE `otp_table` (
  `otp_id` int(50) NOT NULL,
  `email` varchar(255) NOT NULL,
  `otp` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `otp_table`
--

INSERT INTO `otp_table` (`otp_id`, `email`, `otp`) VALUES
(1, 'sanatrock2003@gmail.com', '6331'),
(2, 'sanatrock2003@gmail.com', '6911'),
(3, 'dpbhasme2809@gmail.com', '5338');

-- --------------------------------------------------------

--
-- Table structure for table `passenger`
--

CREATE TABLE `passenger` (
  `pid` int(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `mobile` varchar(255) NOT NULL,
  `dob` date NOT NULL,
  `gender` varchar(255) NOT NULL,
  `id` int(11) NOT NULL,
  `seq_no` int(11) NOT NULL,
  `booking_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `passenger`
--

INSERT INTO `passenger` (`pid`, `name`, `mobile`, `dob`, `gender`, `id`, `seq_no`, `booking_id`) VALUES
(1, 'Mr. Darshan Bhasme', '7774012809', '2003-09-28', 'Male', 1, 3, 1),
(2, 'Mrs. Aastha Bhasme', '7774012809', '2003-11-18', 'Female', 1, 3, 1),
(3, 'Mr. Sanat Pillai', '9359738142', '2003-10-04', 'Male', 2, 1, 2),
(4, 'Miss. Aarya Gaikwad', '9359738142', '2002-11-10', 'Female', 2, 1, 2),
(5, 'Mr. Ganesh Mali', '9021817579', '2003-09-07', 'Male', 3, 7, 3),
(6, 'Miss. Mitali Mahajan', '9021817579', '2002-06-17', 'Female', 3, 7, 3),
(7, 'Mr. Darshan Bhasme', '7774012809', '2003-07-28', 'Male', 3, 7, 3),
(8, 'Mr. Sanat Pillai', '8668090108', '2003-10-04', 'Male', 3, 7, 3),
(9, 'Miss. Aarya Gaikwad', '8668090108', '2002-12-02', 'Female', 3, 7, 3),
(10, 'Mr. Sanat Pillai', '9359738142', '2003-10-04', 'Male', 4, 4, 4),
(11, 'Miss. Aarya Gaikwad', '7666939050', '2002-11-10', 'Female', 4, 4, 4),
(12, 'Mr. Ganesh Mali', '9021817579', '2003-09-07', 'Male', 5, 10, 5),
(13, 'Miss. Mitali Mahajan', '9763306763', '2002-06-17', 'Female', 5, 10, 5),
(14, 'Miss. Shruti Mahajan', '9022325214', '2005-09-20', 'Female', 5, 10, 5),
(15, 'Mr. Virat Kohli', '7676767878', '1990-04-10', 'Male', 1, 12, 6),
(16, 'Mr. Rohit  Sharma', '9359738142', '1999-04-24', 'Male', 1, 12, 6),
(17, 'Miss. Smriti Mandana', '7889898780', '1966-04-01', 'Female', 1, 12, 6),
(18, 'Mr. Kedar jadhav', '9021545421', '1998-02-16', 'Male', 1, 12, 6),
(19, 'Miss. Komal Mahajan', '8208302143', '1999-12-09', 'Female', 5, 13, 7),
(20, 'Mr. Yuvik Mistry', '9021817579', '2020-01-20', 'Male', 5, 13, 7),
(21, 'Mr. Yash Pradhan', '9021817579', '2003-02-01', 'Male', 6, 5, 8),
(22, 'Mr. Virat Kohli', '7775248565', '1989-04-10', 'Male', 6, 2, 9),
(23, 'Mrs. Anushka Kohli', '8985868487', '1992-05-02', 'Female', 6, 2, 9),
(24, 'Miss. Vamika Kohli', '8586848281', '2020-05-02', 'Female', 6, 2, 9);

-- --------------------------------------------------------

--
-- Table structure for table `payment`
--

CREATE TABLE `payment` (
  `payid` int(255) NOT NULL,
  `nop` int(255) NOT NULL,
  `cop` int(255) NOT NULL,
  `fno` int(255) NOT NULL,
  `seq_no` int(255) NOT NULL,
  `id` int(255) NOT NULL,
  `booking_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `payment`
--

INSERT INTO `payment` (`payid`, `nop`, `cop`, `fno`, `seq_no`, `id`, `booking_id`) VALUES
(1, 2, 8001, 401, 3, 1, 1),
(2, 2, 10000, 102, 1, 2, 2),
(3, 5, 1200, 401, 7, 3, 3),
(4, 2, 7000, 202, 4, 4, 4),
(5, 3, 8008, 401, 10, 5, 5),
(6, 4, 9550, 202, 12, 1, 6),
(7, 2, 1000, 401, 13, 5, 7),
(8, 1, 11700, 201, 5, 6, 8),
(9, 3, 15999, 102, 2, 6, 9);

-- --------------------------------------------------------

--
-- Table structure for table `register`
--

CREATE TABLE `register` (
  `id` int(255) NOT NULL,
  `title` varchar(255) NOT NULL DEFAULT 'Mr.',
  `firstname` varchar(255) NOT NULL DEFAULT 'Ganesh',
  `lastname` varchar(255) NOT NULL DEFAULT 'Mali',
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `mobile` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `register`
--

INSERT INTO `register` (`id`, `title`, `firstname`, `lastname`, `email`, `password`, `mobile`) VALUES
(1, 'Mr', 'DARSHAN', 'BHASME', 'dpbhasme2809@gmail.com', '$2y$10$rePPm9iKWDpqxcwCr85wV.AMEzqtf6L.mMYRCSW9wTjecOVRBf9Xu', 7774012809),
(2, 'Mr', 'SANAT', 'PILLAI', 'sanatrock2003@gmail.com', '$2y$10$dPb0JBVGyGKro55qGZI3q.QxLAQQ3aGJreS3wz8l7OSglkpY2jvo2', 9359738142),
(3, 'Miss', 'MITALI', 'MAHAJAN', 'mahajanmitali07@gmail.com', '$2y$10$brDuUovK.ZxsJstkam6Uw.avuha3ZngLPh3y.2xyMIWcWn2SYPaYK', 9021817578),
(4, 'Miss', 'AARYA', 'GAIKWAD', 'aaryagaikwad61@gmail.com', '$2y$10$jVmTaExAJmzQTVOdoXBfzeP9JBPzFffj7KwWvVhCk3YT1XySJM0uW', 7666939050),
(5, 'Miss', 'MITALI', 'MAHAJAN', 'mahajanmitali38@gmail.com', '$2y$10$3DIFK9kyWhqEOaP0waR48eB67lw6U3HgRNNucE1XSfxr4GSoC7aCG', 9763306763),
(6, 'Mr', 'YASH', 'PRADHAN', 'yashpradhan922@gmail.com', '$2y$10$6HBBZsxxeOPeXfSbQGFqtOKByXoOb7.fvApjHTd6v7ED4cI/1jEdC', 9373523657);

-- --------------------------------------------------------

--
-- Table structure for table `seat_cost`
--

CREATE TABLE `seat_cost` (
  `pid` int(11) NOT NULL,
  `price` int(11) NOT NULL,
  `date` date NOT NULL,
  `class_id` int(11) NOT NULL,
  `seq_no` int(11) NOT NULL,
  `classname` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `seat_cost`
--

INSERT INTO `seat_cost` (`pid`, `price`, `date`, `class_id`, `seq_no`, `classname`) VALUES
(1, 5000, '2024-04-07', 4, 1, 'Economy Class'),
(2, 10000, '2024-04-07', 6, 1, 'First Class'),
(3, 20000, '2024-04-07', 5, 1, 'Business Class'),
(4, 3999, '2024-04-07', 4, 2, 'Economy Class'),
(5, 8999, '2024-04-07', 6, 2, 'First Class'),
(6, 15999, '2024-04-07', 5, 2, 'Business Class'),
(7, 5001, '2024-04-07', 13, 3, 'Economy Class'),
(8, 8001, '2024-04-07', 14, 3, 'Business Class'),
(9, 3000, '2024-04-07', 10, 4, 'Economy Class'),
(10, 7000, '2024-04-07', 12, 4, 'First Class'),
(11, 2700, '2024-04-07', 7, 5, 'Economy Class'),
(12, 7700, '2024-04-07', 9, 5, 'First Class'),
(13, 11700, '2024-04-07', 8, 5, 'Business Class'),
(14, 1000, '2024-04-07', 10, 6, 'Economy Class'),
(15, 5000, '2024-04-07', 12, 6, 'First Class'),
(16, 1200, '2024-04-07', 13, 7, 'Economy Class'),
(17, 8200, '2024-04-07', 14, 7, 'Business Class'),
(18, 1011, '2024-04-07', 16, 8, 'Economy Class'),
(19, 5055, '2024-04-07', 18, 8, 'First Class'),
(20, 8088, '2024-04-07', 17, 8, 'Business Class'),
(21, 2001, '2024-04-07', 7, 9, 'Economy Class'),
(22, 6001, '2024-04-07', 9, 9, 'First Class'),
(23, 9001, '2024-04-07', 8, 9, 'Business Class'),
(24, 3003, '2024-04-07', 13, 10, 'Economy Class'),
(25, 8008, '2024-04-07', 14, 10, 'Business Class'),
(26, 3000, '2024-04-08', 16, 11, 'Economy Class'),
(27, 7000, '2024-04-08', 18, 11, 'First Class'),
(28, 9500, '2024-04-08', 17, 11, 'Business Class'),
(29, 7000, '2024-04-08', 10, 12, 'Economy Class'),
(30, 9550, '2024-04-08', 12, 12, 'First Class'),
(31, 1000, '2024-04-08', 13, 13, 'Economy Class'),
(32, 8000, '2024-04-08', 14, 13, 'Business Class'),
(33, 4000, '2024-04-08', 1, 14, 'Economy Class'),
(34, 5000, '2024-04-08', 3, 14, 'First Class'),
(35, 8000, '2024-04-08', 2, 14, 'Business Class'),
(36, 3000, '2024-04-08', 16, 15, 'Economy Class'),
(37, 8999, '2024-04-08', 18, 15, 'First Class'),
(38, 9999, '2024-04-08', 17, 15, 'Business Class');

-- --------------------------------------------------------

--
-- Table structure for table `ticket`
--

CREATE TABLE `ticket` (
  `tno` int(255) NOT NULL,
  `aname` varchar(50) NOT NULL,
  `fno` int(255) NOT NULL,
  `src` varchar(255) NOT NULL,
  `dest` varchar(255) NOT NULL,
  `ddt` datetime NOT NULL,
  `adt` datetime NOT NULL,
  `prn` varchar(255) NOT NULL,
  `cname` varchar(255) NOT NULL,
  `bookdatetime` datetime NOT NULL DEFAULT current_timestamp(),
  `pid` int(255) NOT NULL,
  `id` int(255) NOT NULL,
  `seq_no` int(11) NOT NULL,
  `booking_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `ticket`
--

INSERT INTO `ticket` (`tno`, `aname`, `fno`, `src`, `dest`, `ddt`, `adt`, `prn`, `cname`, `bookdatetime`, `pid`, `id`, `seq_no`, `booking_id`) VALUES
(1, 'AirIndia', 401, 'Amritsar (ATQ)', 'Kochi (KOC)', '2024-04-09 14:15:00', '2024-04-09 18:30:00', '70420240001', 'Business Class', '2024-04-07 17:02:15', 1, 1, 3, 1),
(2, 'AirIndia', 401, 'Amritsar (ATQ)', 'Kochi (KOC)', '2024-04-09 14:15:00', '2024-04-09 18:30:00', '70420240002', 'Business Class', '2024-04-07 17:02:15', 2, 1, 3, 1),
(3, 'Emirates', 102, 'Mumbai (BOM)', 'Hyderabad (HYD)', '2024-04-08 12:10:00', '2024-04-08 14:20:00', '70420240003', 'First Class', '2024-04-07 17:16:21', 3, 2, 1, 2),
(4, 'Emirates', 102, 'Mumbai (BOM)', 'Hyderabad (HYD)', '2024-04-08 12:10:00', '2024-04-08 14:20:00', '70420240004', 'First Class', '2024-04-07 17:16:21', 4, 2, 1, 2),
(5, 'AirIndia', 401, 'Mumbai (BOM)', 'Kolkata (CCU)', '2024-04-11 18:43:00', '2024-04-11 21:30:00', '70420240005', 'Economy Class', '2024-04-07 17:48:43', 5, 3, 7, 3),
(6, 'AirIndia', 401, 'Mumbai (BOM)', 'Kolkata (CCU)', '2024-04-11 18:43:00', '2024-04-11 21:30:00', '70420240006', 'Economy Class', '2024-04-07 17:48:43', 6, 3, 7, 3),
(7, 'AirIndia', 401, 'Mumbai (BOM)', 'Kolkata (CCU)', '2024-04-11 18:43:00', '2024-04-11 21:30:00', '70420240007', 'Economy Class', '2024-04-07 17:48:43', 7, 3, 7, 3),
(8, 'AirIndia', 401, 'Mumbai (BOM)', 'Kolkata (CCU)', '2024-04-11 18:43:00', '2024-04-11 21:30:00', '70420240008', 'Economy Class', '2024-04-07 17:48:43', 8, 3, 7, 3),
(9, 'AirIndia', 401, 'Mumbai (BOM)', 'Kolkata (CCU)', '2024-04-11 18:43:00', '2024-04-11 21:30:00', '70420240009', 'Economy Class', '2024-04-07 17:48:43', 9, 3, 7, 3),
(10, 'IndiGo', 202, 'Amritsar (ATQ)', 'Kochi (KOC)', '2024-04-09 07:30:00', '2024-04-09 10:00:00', '80420240010', 'First Class', '2024-04-08 10:43:53', 10, 4, 4, 4),
(11, 'IndiGo', 202, 'Amritsar (ATQ)', 'Kochi (KOC)', '2024-04-09 07:30:00', '2024-04-09 10:00:00', '80420240011', 'First Class', '2024-04-08 10:43:53', 11, 4, 4, 4),
(12, 'AirIndia', 401, 'Nashik (NSK)', 'Mumbai (BOM)', '2024-04-08 15:00:00', '2024-04-08 17:50:00', '80420240012', 'Business Class', '2024-04-08 10:56:15', 12, 5, 10, 5),
(13, 'AirIndia', 401, 'Nashik (NSK)', 'Mumbai (BOM)', '2024-04-08 15:00:00', '2024-04-08 17:50:00', '80420240013', 'Business Class', '2024-04-08 10:56:15', 13, 5, 10, 5),
(14, 'AirIndia', 401, 'Nashik (NSK)', 'Mumbai (BOM)', '2024-04-08 15:00:00', '2024-04-08 17:50:00', '80420240014', 'Business Class', '2024-04-08 10:56:15', 14, 5, 10, 5),
(15, 'IndiGo', 202, 'Delhi (DEL)', 'Hyderabad (HYD)', '2024-04-13 16:00:00', '2024-04-13 18:05:00', '80420240015', 'First Class', '2024-04-08 11:13:04', 15, 1, 12, 6),
(16, 'IndiGo', 202, 'Delhi (DEL)', 'Hyderabad (HYD)', '2024-04-13 16:00:00', '2024-04-13 18:05:00', '80420240016', 'First Class', '2024-04-08 11:13:04', 16, 1, 12, 6),
(17, 'IndiGo', 202, 'Delhi (DEL)', 'Hyderabad (HYD)', '2024-04-13 16:00:00', '2024-04-13 18:05:00', '80420240017', 'First Class', '2024-04-08 11:13:04', 17, 1, 12, 6),
(18, 'IndiGo', 202, 'Delhi (DEL)', 'Hyderabad (HYD)', '2024-04-13 16:00:00', '2024-04-13 18:05:00', '80420240018', 'First Class', '2024-04-08 11:13:04', 18, 1, 12, 6),
(19, 'AirIndia', 401, 'Chennai (MAA)', 'Amritsar (ATQ)', '2024-04-14 11:00:00', '2024-04-14 13:05:00', '80420240019', 'Economy Class', '2024-04-08 11:16:50', 19, 5, 13, 7),
(20, 'AirIndia', 401, 'Chennai (MAA)', 'Amritsar (ATQ)', '2024-04-14 11:00:00', '2024-04-14 13:05:00', '80420240020', 'Economy Class', '2024-04-08 11:16:50', 20, 5, 13, 7),
(21, 'IndiGo', 201, 'Bengaluru (BGL)', 'Ahmedabad (AMD)', '2024-04-10 04:37:00', '2024-04-10 07:38:00', '80420240021', 'Business Class', '2024-04-08 11:22:53', 21, 6, 5, 8),
(22, 'Emirates', 102, 'Hyderabad (HYD)', 'Kolkata (CCU)', '2024-04-08 23:30:00', '2024-04-09 03:30:00', '80420240022', 'Business Class', '2024-04-08 11:44:05', 22, 6, 2, 9),
(23, 'Emirates', 102, 'Hyderabad (HYD)', 'Kolkata (CCU)', '2024-04-08 23:30:00', '2024-04-09 03:30:00', '80420240023', 'Business Class', '2024-04-08 11:44:05', 23, 6, 2, 9),
(24, 'Emirates', 102, 'Hyderabad (HYD)', 'Kolkata (CCU)', '2024-04-08 23:30:00', '2024-04-09 03:30:00', '80420240024', 'Business Class', '2024-04-08 11:44:05', 24, 6, 2, 9);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin_r`
--
ALTER TABLE `admin_r`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `airline`
--
ALTER TABLE `airline`
  ADD PRIMARY KEY (`ano`);

--
-- Indexes for table `booking`
--
ALTER TABLE `booking`
  ADD PRIMARY KEY (`booking_id`),
  ADD KEY `id` (`id`);

--
-- Indexes for table `class`
--
ALTER TABLE `class`
  ADD PRIMARY KEY (`class_id`),
  ADD KEY `fno` (`fno`);

--
-- Indexes for table `feedback`
--
ALTER TABLE `feedback`
  ADD PRIMARY KEY (`seq_no`),
  ADD KEY `id` (`id`);

--
-- Indexes for table `flight`
--
ALTER TABLE `flight`
  ADD PRIMARY KEY (`seq_no`),
  ADD UNIQUE KEY `fno` (`fno`),
  ADD KEY `ano` (`ano`);

--
-- Indexes for table `flight_availabality`
--
ALTER TABLE `flight_availabality`
  ADD PRIMARY KEY (`s`),
  ADD KEY `seq_no` (`seq_no`);

--
-- Indexes for table `flight_schedule`
--
ALTER TABLE `flight_schedule`
  ADD PRIMARY KEY (`seq_no`);

--
-- Indexes for table `otp_table`
--
ALTER TABLE `otp_table`
  ADD PRIMARY KEY (`otp_id`);

--
-- Indexes for table `passenger`
--
ALTER TABLE `passenger`
  ADD PRIMARY KEY (`pid`),
  ADD KEY `id` (`id`),
  ADD KEY `seq_no` (`seq_no`),
  ADD KEY `booking_id` (`booking_id`);

--
-- Indexes for table `payment`
--
ALTER TABLE `payment`
  ADD PRIMARY KEY (`payid`),
  ADD KEY `seq_no` (`seq_no`),
  ADD KEY `id` (`id`),
  ADD KEY `booking_id` (`booking_id`);

--
-- Indexes for table `register`
--
ALTER TABLE `register`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `seat_cost`
--
ALTER TABLE `seat_cost`
  ADD PRIMARY KEY (`pid`),
  ADD KEY `seq_no` (`seq_no`);

--
-- Indexes for table `ticket`
--
ALTER TABLE `ticket`
  ADD PRIMARY KEY (`tno`),
  ADD KEY `pid` (`pid`),
  ADD KEY `id` (`id`),
  ADD KEY `seq_no` (`seq_no`),
  ADD KEY `booking_id` (`booking_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin_r`
--
ALTER TABLE `admin_r`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `airline`
--
ALTER TABLE `airline`
  MODIFY `ano` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `booking`
--
ALTER TABLE `booking`
  MODIFY `booking_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `class`
--
ALTER TABLE `class`
  MODIFY `class_id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `feedback`
--
ALTER TABLE `feedback`
  MODIFY `seq_no` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `flight`
--
ALTER TABLE `flight`
  MODIFY `seq_no` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `flight_availabality`
--
ALTER TABLE `flight_availabality`
  MODIFY `s` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `flight_schedule`
--
ALTER TABLE `flight_schedule`
  MODIFY `seq_no` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `otp_table`
--
ALTER TABLE `otp_table`
  MODIFY `otp_id` int(50) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `passenger`
--
ALTER TABLE `passenger`
  MODIFY `pid` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `payment`
--
ALTER TABLE `payment`
  MODIFY `payid` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `register`
--
ALTER TABLE `register`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `seat_cost`
--
ALTER TABLE `seat_cost`
  MODIFY `pid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;

--
-- AUTO_INCREMENT for table `ticket`
--
ALTER TABLE `ticket`
  MODIFY `tno` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `booking`
--
ALTER TABLE `booking`
  ADD CONSTRAINT `booking_ibfk_1` FOREIGN KEY (`id`) REFERENCES `register` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `class`
--
ALTER TABLE `class`
  ADD CONSTRAINT `class_ibfk_1` FOREIGN KEY (`fno`) REFERENCES `flight` (`fno`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `feedback`
--
ALTER TABLE `feedback`
  ADD CONSTRAINT `feedback_ibfk_1` FOREIGN KEY (`id`) REFERENCES `register` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `flight`
--
ALTER TABLE `flight`
  ADD CONSTRAINT `flight_ibfk_1` FOREIGN KEY (`ano`) REFERENCES `airline` (`ano`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `flight_availabality`
--
ALTER TABLE `flight_availabality`
  ADD CONSTRAINT `flight_availabality_ibfk_1` FOREIGN KEY (`seq_no`) REFERENCES `flight_schedule` (`seq_no`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `passenger`
--
ALTER TABLE `passenger`
  ADD CONSTRAINT `passenger_ibfk_1` FOREIGN KEY (`seq_no`) REFERENCES `flight_schedule` (`seq_no`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `passenger_ibfk_2` FOREIGN KEY (`id`) REFERENCES `register` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `payment`
--
ALTER TABLE `payment`
  ADD CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`seq_no`) REFERENCES `flight_schedule` (`seq_no`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `payment_ibfk_2` FOREIGN KEY (`id`) REFERENCES `register` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `seat_cost`
--
ALTER TABLE `seat_cost`
  ADD CONSTRAINT `seat_cost_ibfk_1` FOREIGN KEY (`seq_no`) REFERENCES `flight_schedule` (`seq_no`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `ticket`
--
ALTER TABLE `ticket`
  ADD CONSTRAINT `ticket_ibfk_1` FOREIGN KEY (`pid`) REFERENCES `passenger` (`pid`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `ticket_ibfk_2` FOREIGN KEY (`id`) REFERENCES `register` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `ticket_ibfk_3` FOREIGN KEY (`seq_no`) REFERENCES `flight_schedule` (`seq_no`) ON DELETE NO ACTION ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
