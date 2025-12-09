-- BidVerse Database Structure
-- Microsoft SQL Server 2019+
-- Database: BidVerse
-- Schema: dbo

USE master;
GO

-- Create database if not exists
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'BidVerse')
BEGIN
    CREATE DATABASE BidVerse;
END
GO

USE BidVerse;
GO

-- ================================================
-- Table: User
-- ================================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[User]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[User] (
        [userId] INT IDENTITY(1,1) NOT NULL,
        [userName] NVARCHAR(25) NOT NULL,
        [emailAddress] NVARCHAR(50) NOT NULL UNIQUE,
        [phoneNumber] NVARCHAR(16) NOT NULL,
        [role] NVARCHAR(20) NOT NULL,
        CONSTRAINT [PK_User] PRIMARY KEY CLUSTERED ([userId] ASC)
    );
END
GO

-- ================================================
-- Table: Category
-- ================================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Category]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[Category] (
        [categoryId] INT IDENTITY(1,1) NOT NULL,
        [categoryName] NVARCHAR(50) NOT NULL,
        CONSTRAINT [PK_Category] PRIMARY KEY CLUSTERED ([categoryId] ASC)
    );
END
GO

-- ================================================
-- Table: Auction
-- ================================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Auction]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[Auction] (
        [itemId] INT IDENTITY(1,1) NOT NULL,
        [ownerId] INT NOT NULL,
        [categoryId] INT NOT NULL,
        [itemName] NVARCHAR(50) NOT NULL,
        [minimumPrice] DECIMAL(12, 2) NOT NULL,
        [createDate] DATETIME2 NOT NULL,
        [expiredDate] DATETIME2 NOT NULL,
        [lastBid] DECIMAL(12, 2) NULL,
        [description] NVARCHAR(MAX) NOT NULL,
        [type] NVARCHAR(20) NOT NULL,
        [extraTime] INT NULL,
        [itemState] NVARCHAR(20) NOT NULL,
        [tags] NVARCHAR(MAX) NULL,
        [minStep] INT NULL,
        [condition] INT NOT NULL,
        [version] BIGINT NOT NULL DEFAULT 0,
        [startDate] DATETIME2 NULL,
        CONSTRAINT [PK_Auction] PRIMARY KEY CLUSTERED ([itemId] ASC),
        CONSTRAINT [FK_Auction_Owner] FOREIGN KEY ([ownerId]) REFERENCES [dbo].[User]([userId]),
        CONSTRAINT [FK_Auction_Category] FOREIGN KEY ([categoryId]) REFERENCES [dbo].[Category]([categoryId])
    );
END
GO

-- ================================================
-- Table: Bid
-- ================================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Bid]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[Bid] (
        [bidId] INT IDENTITY(1,1) NOT NULL,
        [auctionId] INT NOT NULL,
        [bidderId] INT NOT NULL,
        [value] DECIMAL(12, 2) NOT NULL,
        [timeStamp] DATETIME2 NOT NULL,
        [isWinning] BIT NOT NULL,
        CONSTRAINT [PK_Bid] PRIMARY KEY CLUSTERED ([bidId] ASC),
        CONSTRAINT [FK_Bid_Auction] FOREIGN KEY ([auctionId]) REFERENCES [dbo].[Auction]([itemId]) ON DELETE CASCADE,
        CONSTRAINT [FK_Bid_Bidder] FOREIGN KEY ([bidderId]) REFERENCES [dbo].[User]([userId])
    );
END
GO

-- ================================================
-- Table: AutoBid
-- ================================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[AutoBid]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[AutoBid] (
        [id] INT IDENTITY(1,1) NOT NULL,
        [auctionId] INT NOT NULL,
        [userId] INT NOT NULL,
        [maxBid] DECIMAL(18, 2) NULL,
        [minIncrement] DECIMAL(18, 2) NULL,
        [maxIncrement] DECIMAL(18, 2) NULL,
        [startTime] DATETIME2 NULL,
        [endTime] DATETIME2 NULL,
        [isActive] BIT NOT NULL,
        [conditionsJson] NVARCHAR(MAX) NULL,
        [lastBidAmount] DECIMAL(18, 2) NULL,
        [createdAt] DATETIME2 NOT NULL,
        [updatedAt] DATETIME2 NULL,
        CONSTRAINT [PK_AutoBid] PRIMARY KEY CLUSTERED ([id] ASC),
        CONSTRAINT [FK_AutoBid_Auction] FOREIGN KEY ([auctionId]) REFERENCES [dbo].[Auction]([auctionId]) ON DELETE CASCADE,
        CONSTRAINT [FK_AutoBid_User] FOREIGN KEY ([userId]) REFERENCES [dbo].[User]([userId])
    );
END
GO

-- ================================================
-- Table: Notification
-- ================================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Notification]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[Notification] (
        [id] INT IDENTITY(1,1) NOT NULL,
        [userId] INT NOT NULL,
        [auctionId] INT NULL,
        [timestamp] DATETIME2 NOT NULL,
        [message] NVARCHAR(499) NOT NULL,
        [link] NVARCHAR(99) NULL,
        [isRead] BIT NOT NULL,
        CONSTRAINT [PK_Notification] PRIMARY KEY CLUSTERED ([id] ASC),
        CONSTRAINT [FK_Notification_User] FOREIGN KEY ([userId]) REFERENCES [dbo].[User]([userId]),
        CONSTRAINT [FK_Notification_Auction] FOREIGN KEY ([auctionId]) REFERENCES [dbo].[Auction]([auctionId]) ON DELETE SET NULL
    );
END
GO

-- ================================================
-- Table: AuctionImages
-- ================================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[AuctionImages]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[AuctionImages] (
        [id] INT IDENTITY(1,1) NOT NULL,
        [auctionId] INT NOT NULL,
        [imageUrl] NVARCHAR(500) NOT NULL,
        [isMainImage] BIT NOT NULL,
        [uploadedAt] DATETIME2 NOT NULL,
        [uploadedByUserId] INT NOT NULL,
        [publicId] NVARCHAR(50) NOT NULL,
        CONSTRAINT [PK_AuctionImages] PRIMARY KEY CLUSTERED ([id] ASC),
        CONSTRAINT [FK_AuctionImages_Auction] FOREIGN KEY ([auctionId]) REFERENCES [dbo].[Auction]([itemId]) ON DELETE CASCADE,
        CONSTRAINT [FK_AuctionImages_User] FOREIGN KEY ([uploadedByUserId]) REFERENCES [dbo].[User]([userId])
    );
END
GO