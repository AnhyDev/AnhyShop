# AnhyShop
Is a unique plugin for creating merchants and trading on Minecraft servers, based on the trading inventory of villagers. Tested on `Spigot`, `Paper`, `Purpur` server cores, version `1.20.2`.

### Essential Dependencies for [**AnhyShop**](https://dev.anh.ink/anhyshop/)
The primary requirement for **AnhyShop** to function is the installation of the [**AnhyLibAPI**](https://dev.anh.ink/anhylibapi/) library version 1.5.2 or higher. Without **AnhyLibAPI**, **AnhyShop** will not operate. It provides translation of system messages and multilingual names for merchants.

Additionally, **AnhyShop** is compatible with [**AnhyLingo**](https://dev.anh.ink/anhylingo/) version 0.3.3 or higher. Installing **AnhyLingo** expands the multilingual capabilities of **AnhyShop**. However, its absence does not affect the primary operation of **AnhyShop**.

## Functionality of the Plugin
**AnhyShop: Intuitive Plugin for Creating Administrative Stores in Minecraft**

- **Simplicity and Convenience**: No complicated configurations, all data is automatically stored in the embedded SQLite database (file `shops.db` in the plugin folder).
- **Data Security**: Information about traders is encrypted.
- **Flexible Use**: Open trade via console commands or player commands with appropriate permissions.

### Administrator Commands
(with permission `anhyshop.*`)

#### Reloading Commands
- **Reloading the plugin** `(anhyshop.reload)`: `/shop reload` - Reloads the language files, configurations, and traders from the database. Available from the console.

### Work with Traders
(with permission `anhyshop.trader.*`)

1. **Information about traders** `(anhyshop.trader.view)`: `/shop list` - Displays a list of all traders. Available from the console.
2. **Create a new trader** `(anhyshop.trader.create)`: `/shop newt <name>` - Creates a new trader. Available from the console.
3. **Remove trader** `(anhyshop.trader.delete)`: `/shop delt <key>` - Deletes a trader. Available from the console.
4. **Rename trader** `(anhyshop.trader.rename)`: `/shop rename <key> <new_name>` - Renames a trader. Available from the console.
5. **Open trade inventory** `(anhyshop.trader.open)`: `/shop open <key> <player_name>` - Opens trade for a player. Available from the console.
6. **Trade by yourself** `(anhyshop.trader.trade)`: `/shop trade <key>` - Opens a trade.

### Operations with Goods
(with permission `anhyshop.product.*`)

1. **Add a new trade** `(anhyshop.product.add)`: `/shop add <key>` - To add a trade, place items in slots 1, 2, 3 of your inventory.
2. **Replace an existing trade** `(anhyshop.product.replace)`: `/shop replace <key>` - Replace or add products.
3. **Delete an existing trade** `(anhyshop.product.remove)`: `/shop remove <key>` - Remove a product from bidding.

![Add Trade](https://dev.anh.ink/images/addtrade.png "Work with traders")

### Directory Structure
- `lang`: Files with translations of plugin system messages and translations of trader names.
```yaml
trader_name_emberlyn: Emberlyn Gemwhisper
trader_name_flint: Flint Firebeard
trader_name_mira: Mira Mysticsight
trader_name_silas: Silas Silverthread
trader_name_cedric: Cedric Stonehand
trader_name_willow: Willow Wildroot
trader_name_raven: Raven Nightshade
trader_name_eldon: Eldon Ironfoot
trader_name_aurora: Aurora Skydancer
trader_name_gideon: Gideon Goldspinner
```
### Expanded Functionality of AnhyShop Plugin Version 1.0.0

**AnhyShop** in version 1.0.0 introduces a new concept of "Sellers," which allow players to interactively trade with merchants through in-game objects. This enhances the trading experience by providing a more dynamic and convenient management of trading processes on the server.

#### Key Features of "Sellers":

1. **Types of "Sellers"**:
   - **Mechanical Objects**: Buttons, doors, levers are identified by their type and the exact coordinates of their location.
   - **Signs**: Identification is based on the material type and the text displayed on the sign. Signs can be placed in different locations, but the text on them will determine a single "Seller."
   - **Mobs with Custom Names**: Utilizes the type of mob and its unique name for identification. Only mobs with custom names can become "Sellers."
   - **Wandering Traders**: All wandering traders, once linked, are recognized as one "Seller," allowing for a uniform change to their trading inventories.
   - **Villagers**: Identified by profession and level. Creating a "Seller" from a villager of a specific profession and level replaces standard trade operations for all similar villagers.

#### Commands to Manage "Sellers":

**Note**: All the commands listed below are available only to players with the `anhyshop.seller` permission.

- **Creating a "Seller"**:
  - **Command**: `/shop seller add <trader_key>`
  - **Description**: Creates a "Seller" from the object the player is looking at, linking it to a selected trader using a unique key `<trader_key>`. The player must be within 5 blocks of the object.

- **Removing a "Seller"**:
  - **Command**: `/shop seller remove <seller_id>`
  - **Description**: Removes a "Seller" using its unique numerical identifier `<seller_id>`.

- **Viewing Information about a "Seller"**:
  - **Command**: `/shop seller view <seller_id>`
  - **Description**: Displays detailed information about the "Seller," including its type, location, linked trader, and identifier.

- **Listing All "Sellers"**:
  - **Command**: `/shop seller list`
  - **Description**: Displays a list of all existing "Sellers," with the ability to copy `<seller_id>` to the clipboard, simplifying the management of identifiers for further operations.

These updates significantly enhance player interactions with traders, providing greater control and customization options for trading processes on Minecraft servers.

### Language Personalization
**Multilingual Functionality in AnhyShop with AnhyLibAPI and AnhyLingo Integration**

1. **System Messages Localization** (AnhyLibAPI): Stored in `shop_XX.yml` files within the `lang` folder.
2. **Trader Names Translation** (AnhyLibAPI): Add translations in `traders_XX.yml`.
3. **Multilingual Item Display in Trades** (AnhyLingo): Trades involving multilingual items display these items in the player's chosen language.

##### AnhyShop's integration with AnhyLibAPI and AnhyLingo enhances usability across different languages and enriches the trading experience, making it inclusive and globally accessible.
