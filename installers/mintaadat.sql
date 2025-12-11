-- BidVerse Sample Data (Mintaadatok)
-- Ez a fájl a structure.sql futtatása UTÁN futtatandó

USE BidVerse;
GO

-- ================================================
-- User (Felhasználók)
-- ================================================
SET IDENTITY_INSERT [dbo].[User] ON;
GO

INSERT INTO [dbo].[User] ([userId], [userName], [emailAddress], [phoneNumber], [role])
VALUES 
    (1, 'newUserName', 'newEmailgf@example.com', '06201111111', 'USER'),
    (2, 'newUserhhName', 'newEmailgggf@example.com', '06202222222', 'USER'),
    (12, 'testUser1', 'user1@gmail.com', '06301234567', 'USER'),
    (13, 'testUser2', 'user2@gmail.com', '06307654321', 'USER'),
    (14, 'testUser3', 'user3@gmail.com', '06209876543', 'USER'),
    (15, 'testUser4', 'user4@gmail.com', '06705556677', 'USER'),
    (1004, 'Zoltán Benedek', 'benedekzoltan2003@gmail.com', '06203333333', 'USER'),
    (1005, 'fiók plusz', 'pluszfiok001@gmail.com', '06204444444', 'USER'),
    (1006, 'Teszt profil', 'sajat.tesztprofil@gmail.com', '06205555555', 'USER');
GO

SET IDENTITY_INSERT [dbo].[User] OFF;
GO

-- ================================================
-- Category (Kategóriák)
-- ================================================
SET IDENTITY_INSERT [dbo].[Category] ON;
GO

INSERT INTO [dbo].[Category] ([categoryId], [categoryName])
VALUES 
    (0, 'Elektronika'),
    (1, 'Ékszerek és Órák'),
    (2, 'Művészet és Gyűjteményes tárgyak'),
    (3, 'Bútor'),
    (4, 'Járművek'),
    (5, 'Ruházat és Kiegészítők'),
    (6, 'Sport és Szabadtéri'),
    (7, 'Játékok és Játéktermi játékok'),
    (8, 'Otthon és Kert'),
    (9, 'Könyvek és Médiatartalmak'),
    (10, 'Antikvitás'),
    (11, 'Érmék és Bélyegek'),
    (12, 'Bor és Alkoholos italok'),
    (13, 'Hangszerek'),
    (14, 'Irodai felszerelés');
GO

SET IDENTITY_INSERT [dbo].[Category] OFF;
GO

-- ================================================
-- Auctions (Aukciók) - Valós adatok
-- ================================================
SET IDENTITY_INSERT [dbo].[Auction] ON;
GO

INSERT INTO [dbo].[Auction] ([itemId], [ownerId], [categoryId], [itemName], [minimumPrice], [createDate], [expiredDate], [lastBid], [description], [type], [extraTime], [itemState], [tags], [minStep], [condition], [version], [startDate])
VALUES 
    (5063, 1004, 3, 'Fiókos szekrény', 100.00, '2025-11-05 16:15:42.413', '2025-11-13 16:15:00.000', NULL, 'Up for auction is this practical and versatile three-drawer filing cabinet in a light wood-grain finish! This sturdy unit is perfect for home offices, student dorms, or anyone looking to organize their workspace.

The cabinet features three smooth-gliding drawers, offering ample space for files, stationery, and other essentials. The top drawer even comes with a lock for added security of your important documents. The cabinet sits on casters/wheels, making it easy to move around your space as needed.

Cosmetically, the cabinet is in good condition, but it does have a blemish/chip on one side. This is purely cosmetic.

This filing cabinet is ideal for students setting up their study area, remote workers looking to keep their home office organized, or anyone needing a compact storage solution. Don''t miss out on the opportunity to bid on this functional and affordable storage piece!
', 'FIXED', NULL, 'Like new', NULL, 20, 67, 0, NULL),
    (5064, 1004, 3, 'Laptop', 120.00, '2025-11-05 16:22:47.577', '2025-11-18 16:22:00.000', 131.00, 'Offered here is a well-maintained Dell Latitude laptop, perfect for students, professionals, or anyone needing a reliable machine for everyday tasks. While it''s got a few stickers on the lid – think of them as conversation starters, or easily removed if they aren''t your style – this laptop offers solid performance and durability you''d expect from the Latitude line.

Cosmetically, it''s in good shape as you can see in the pictures. There are some signs of normal use as you''d expect from a used laptop, but nothing that impacts functionality. The screen is bright and clear, and the keyboard feels great.

This Latitude is ready to go, just power it on and you''re set. If you are looking for a workhorse laptop without breaking the bank this is an excellent option.

Happy bidding!
', 'FIXED', NULL, 'Well used', NULL, 10, 30, 1, NULL),
    (5065, 1004, 5, 'Lábszárvédők', 30.00, '2025-12-05 16:55:45.217', '2026-01-14 16:55:00.000', NULL, 'Fekete színű lábszárvédők eladók, nem túl használtak, kis méretű. Ajánlott műfüves cipőhöz.', 'FIXED', NULL, 'Brand new', NULL, 3, 74, 0, '2025-11-09 18:05:00.000'),
    (5066, 1004, 5, '67w-os fehér xiaomi töltő', 10.00, '2025-12-28 18:09:43.680', '2025-12-30 23:09:00.000', 13.00, 'Up for auction is a genuine 67W Xiaomi fast charger.', 'FIXED', NULL, 'Brand new', NULL, 2, 96, 1, '2025-11-11 18:09:00.000'),
    (5068, 1004, 3, 'Pop-corn gép eladó', 100.00, '2025-11-05 18:14:24.683', '2025-11-16 18:14:00.000', 195.00, 'Up for auction is a purely functional Sencor Popcorn Maker.', 'FIXED', NULL, 'Lightly used', NULL, 40, 59, 4, NULL),
    (5069, 1005, 2, 'Digital food art', 10.00, '2025-11-05 18:23:23.313', '2025-11-05 18:25:00.000', NULL, 'Digital art of food. Unique on the internet.', 'FIXED', NULL, 'Brand new', NULL, 3, 100, 0, NULL),
    (5070, 1004, 3, 'Old armchair', 25.00, '2025-11-05 18:25:56.137', '2025-11-17 18:25:00.000', NULL, 'Used, old almchair for sale.', 'FIXED', NULL, 'Heavily used', NULL, 5, 7, 0, NULL),
    (5071, 1005, 2, 'Digital food art, transparent', 10.00, '2025-11-05 18:32:16.667', '2026-01-10 18:32:00.000', 15.00, 'Digital art, transparent background. Exceptionally beautiful.', 'FIXED', NULL, 'Brand new', NULL, 4, 100, 1, NULL),
    (5072, 1004, 3, 'Kollégiumi szék', 40.00, '2026-11-05 20:11:38.273', '2026-11-11 20:09:00.000', NULL, 'Up for auction is a sturdy, multi-purpose office chair, perfect for the budget-conscious student, home office user, or anyone needing reliable seating without breaking the bank. This chair has seen some use, and while it''s not pristine, it''s structurally sound and ready for many more hours of service.

The chair features a comfortable, padded seat and back, ideal for long study sessions or workdays. The frame is robust, designed to withstand daily wear and tear.

Now, let''s be upfront about the condition: as you can see from the photos, the seat shows signs of use, including some wear and potential staining. A good cleaning could improve its appearance. The frame may also show minor scuffs.

However, dont let that deter you! This chair is all about functionality and value. It''s a great option for a workshop, garage, or as a spare chair when you need extra seating. If you''re not overly concerned about aesthetics and just need a reliable chair, this is the one for you. It''s a practical, no-frills option for someone who values function over form. Don''t miss out on this opportunity to grab a functional chair at a bargain price!
', 'FIXED', NULL, 'Well used', NULL, 90, 29, 0, NULL),
    (5073, 1006, 3, 'Serpenyő fekete', 100.00, '2025-11-05 23:13:14.587', '2025-11-07 23:13:00.000', NULL, 'Up for auction is this reliable and well-used non-stick frying pan, ready to find its new home in your kitchen. This pan has seen some action and has a few scratches on the non-stick surface, as you can see in the close-up photo, but it still has plenty of life left for everyday cooking.

The pan features a sturdy handle for a comfortable grip and even heat distribution for consistent cooking. While the bottom shows signs of use with some discoloration, it doesnt affect its performance on the stovetop. 

Perfect for the budget-conscious cook or student starting out, this frying pan offers a practical and affordable solution for whipping up your favorite meals. It''s ready to handle your omelets, stir-fries, and everything in between! Get bidding!
', 'FIXED', NULL, 'Lightly used', NULL, 10, 67, 0, NULL),
    (5074, 1004, 2, 'Antik rókás bronz kiegészítő', 150.00, '2025-11-18 00:01:55.260', '2025-11-18 18:01:00.000', 175.00, 'Offered here is a meticulously detailed miniature bronze fox figurine, perfect for collectors, miniature enthusiasts, or anyone who appreciates fine craftsmanship. This little fox captures the essence of the creature in a curled-up, resting pose, with its pointed ears and alert expression.

The bronze has a lovely, slightly aged patina, giving it an antique feel without appearing worn or damaged. The intricate details in the fur and facial features demonstrate the skill that went into its creation. Its small size makes it ideal for displaying on a desk, shelf, or curio cabinet. It is in excellent condition. This charming figurine is sure to bring a touch of wild beauty to any space.
', 'FIXED', NULL, 'Brand new', NULL, 20, 54, 2, NULL),
    (5075, 1005, 2, 'Kaja', 120.00, '2025-01-05 19:02:37.113', '2025-12-05 23:02:00.000', NULL, 'Kajás kép.', 'FIXED', NULL, 'Brand new', NULL, 10, 66, 0, '2025-12-05 19:04:00.000'),
    (5076, 1004, 3, 'Karosszék', 30.00, '2026-02-06 10:47:14.930', '2026-12-13 10:47:00.000', 67.00, 'Rossz karosszék.', 'FIXED', NULL, 'Heavily used', NULL, 10, 30, 4, NULL);
GO

SET IDENTITY_INSERT [dbo].[Auction] OFF;
GO

-- ================================================
-- AuctionImages (Aukció képek) - Cloudinary képek
-- ================================================
SET IDENTITY_INSERT [dbo].[AuctionImages] ON;
GO

INSERT INTO [dbo].[AuctionImages] ([id], [auctionId], [cloudinaryUrl], [isPrimary], [orderIndex], [uploadedBy], [fileSizeKb], [format])
VALUES 
    (3059, 5063, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762355753/auction_images/auction_1762355752578_d57329b0-7147-478e-9f9c-088158dfc462.png', 1, 0, 1004, 547, 'png'),
    (3060, 5063, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762355756/auction_images/auction_1762355755378_41930846-21bf-4759-8f62-157faa65a542.png', 0, 1, 1004, 631, 'png'),
    (3061, 5063, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762355757/auction_images/auction_1762355757394_d7dc8b82-4ca8-419b-82c7-4de7b916649d.jpg', 0, 2, 1004, 57, 'jpg'),
    (3062, 5063, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762355759/auction_images/auction_1762355758886_ee48b832-6781-4738-957b-95fd82d60e1d.png', 0, 3, 1004, 556, 'png'),
    (3063, 5063, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762355761/auction_images/auction_1762355761262_b15f507d-c185-4758-b866-e572d12f3ed4.jpg', 0, 4, 1004, 56, 'jpg'),
    (3064, 5064, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762356172/auction_images/auction_1762356172316_4c581659-7142-40b9-bfe4-2989b10da2ec.jpg', 1, 0, 1004, 51, 'jpg'),
    (3065, 5064, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762356174/auction_images/auction_1762356173805_63b5b81a-5d64-4807-9211-fcb4641a12ec.jpg', 0, 1, 1004, 60, 'jpg'),
    (3066, 5065, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762362351/auction_images/auction_1762362350833_aa50e9c3-8e29-43df-9839-0c5271cf4424.jpg', 1, 0, 1004, 47, 'jpg'),
    (3067, 5065, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762362353/auction_images/auction_1762362352589_dc541f8c-f403-4230-81ee-df60ba2dc064.jpg', 0, 1, 1004, 69, 'jpg'),
    (3068, 5066, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762362590/auction_images/auction_1762362588636_b6a3145e-8f82-434f-9456-4d77bebbcc6b.jpg', 1, 0, 1004, 61, 'jpg'),
    (3069, 5066, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762362592/auction_images/auction_1762362591848_da5f7f38-d7d9-4fd2-9952-b34ef132a37c.jpg', 0, 1, 1004, 49, 'jpg'),
    (3073, 5068, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762362870/auction_images/auction_1762362870068_3dd87e7a-d804-43c8-bff3-811094be64a3.jpg', 1, 0, 1004, 46, 'jpg'),
    (3074, 5068, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762362871/auction_images/auction_1762362871570_e967090c-2f51-4ca7-9468-c496c3f84efe.jpg', 0, 1, 1004, 46, 'jpg'),
    (3075, 5068, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762362873/auction_images/auction_1762362873062_ae391205-9f11-49fa-95cb-2f6c793fdf32.jpg', 0, 2, 1004, 69, 'jpg'),
    (3076, 5069, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762363407/auction_images/auction_1762363406434_77a9f7d8-3198-472d-a6e4-4901b396e8ec.png', 1, 0, 1005, 360, 'png'),
    (3077, 5070, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762363560/auction_images/auction_1762363559783_e97e83f8-1265-45a8-9d62-c5fb11babc79.jpg', 1, 0, 1004, 67, 'jpg'),
    (3078, 5071, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762363941/auction_images/auction_1762363940420_21acad92-acbb-4c09-96df-5caadadf9693.png', 1, 0, 1005, 524, 'png'),
    (3079, 5072, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762369905/auction_images/auction_1762369905267_d58cbd9a-b807-4525-be5d-369f81e1453d.jpg', 1, 0, 1004, 84, 'jpg'),
    (3080, 5072, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762369907/auction_images/auction_1762369907621_84cdaa6e-6680-4954-b2fd-56085b79f528.jpg', 0, 1, 1004, 96, 'jpg'),
    (3081, 5072, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762369909/auction_images/auction_1762369909210_c4ffb553-b985-4dc7-953e-14a2d5e7a36d.jpg', 0, 2, 1004, 123, 'jpg'),
    (3082, 5072, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762369911/auction_images/auction_1762369911016_a0ea521d-4e83-4466-9aba-22014eeba73f.jpg', 0, 3, 1004, 127, 'jpg'),
    (3083, 5073, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762380802/auction_images/auction_1762380802185_b02b9e72-5594-4579-bdd1-a6fa25782b5e.jpg', 1, 0, 1006, 84, 'jpg'),
    (3084, 5073, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762380804/auction_images/auction_1762380803902_25e80bee-c3d7-4ec6-b7aa-2d757383e2de.jpg', 0, 1, 1006, 43, 'jpg'),
    (3085, 5073, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1762380805/auction_images/auction_1762380805360_dbefe27c-c0e4-40a5-b803-5887b086c268.jpg', 0, 2, 1006, 57, 'jpg'),
    (3086, 5074, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1763420522/auction_images/auction_1763420521915_dd78fbde-7046-4bee-8119-0240ef859345.jpg', 1, 0, 1004, 45, 'jpg'),
    (3087, 5074, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1763420523/auction_images/auction_1763420523927_8d87272b-3aad-41c0-8544-6897a8b1ee4e.jpg', 0, 1, 1004, 35, 'jpg'),
    (3088, 5074, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1763420525/auction_images/auction_1763420525287_e3b00c10-e897-4194-9731-3a5c8a72f19e.jpg', 0, 2, 1004, 61, 'jpg'),
    (3089, 5075, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1764957761/auction_images/auction_1764957760922_a5ed5aac-3367-4ea3-8e6e-b30a6580c99e.png', 1, 0, 1005, 360, 'png'),
    (3090, 5076, 'https://res.cloudinary.com/dadtzsdlx/image/upload/v1765014441/auction_images/auction_1765014440075_06b950a6-e538-4b0e-a2e4-6f8a1de7b6e4.jpg', 1, 0, 1004, 67, 'jpg');
GO

SET IDENTITY_INSERT [dbo].[AuctionImages] OFF;
GO

-- ================================================
-- Bid (Licitek)
-- ================================================
SET IDENTITY_INSERT [dbo].[Bid] ON;
GO

INSERT INTO [dbo].[Bid] ([bidId], [auctionId], [userId], [value], [timeStamp], [isWinning], [version])
VALUES 
    (1051, 5071, 1004, 15.00, '2025-11-05 18:32:51.613', 1, 0),
    (1053, 5068, 1005, 150.00, '2025-11-05 20:47:28.540', 0, 1),
    (1054, 5068, 1006, 195.00, '2025-11-05 23:09:48.640', 1, 0),
    (1056, 5064, 1005, 131.00, '2025-11-17 22:16:52.120', 1, 0),
    (1057, 5066, 1005, 13.00, '2025-11-17 23:39:31.273', 1, 0),
    (1058, 5074, 1005, 175.00, '2025-11-18 00:14:14.170', 1, 0),
    (1059, 5076, 1005, 35.00, '2025-12-06 10:48:36.727', 0, 1),
    (1060, 5076, 1005, 46.00, '2025-12-06 10:48:58.950', 0, 1),
    (1061, 5076, 1005, 57.00, '2025-12-06 10:49:09.543', 0, 1),
    (1062, 5076, 1006, 67.00, '2025-12-06 10:52:16.930', 1, 0);
GO

SET IDENTITY_INSERT [dbo].[Bid] OFF;
GO

-- ================================================
-- Notification (Értesítések/Üzenetek)
-- ================================================
SET IDENTITY_INSERT [dbo].[Notification] ON;
GO

INSERT INTO [dbo].[Notification] ([id], [senderId], [receiverId], [auctionId], [createdAt], [messageText], [titleText], [alreadyOpened])
VALUES 
    (11, 1004, 1004, NULL, '2025-11-05 20:18:40.677', 'Heló! Ez a levél egy teszt, remélhetőleg minden remekül halad. Ezt most meg kéne kapjam és én magam kéne legyek a feladója.', 'Teszt levél', 1),
    (12, 1004, 1004, NULL, '2025-11-05 20:20:12.787', 'Heló! A teszt sikeres volt, ez most egy válasz rá!', 'Válasz levél', 1),
    (13, 1004, 1004, NULL, '2025-11-05 23:15:47.573', 'Helo, reply 1', 'Test', 1),
    (14, 1006, 1006, NULL, '2025-11-05 23:16:14.183', 'Heloka, 2', 'Reply 1', 1);
GO

SET IDENTITY_INSERT [dbo].[Notification] OFF;
GO

PRINT 'User adatok betöltve: 9 felhasználó';
PRINT 'Category adatok betöltve: 15 kategória';
PRINT 'Auction adatok betöltve: 13 aukció';
PRINT 'AuctionImages adatok betöltve: 29 kép';
PRINT 'Bid adatok betöltve: 10 licit';
PRINT 'Notification adatok betöltve: 4 értesítés';
GO
