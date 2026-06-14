package com.friends.sharing.controller;

import com.friends.sharing.configuration.security.UserAdapter;
import com.friends.sharing.dto.request.*;
import com.friends.sharing.dto.response.*;
import com.friends.sharing.service.FriendsSharingService;
import com.friends.sharing.service.UserDetailsServiceImp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class FriendsSharingController {

    private final UserDetailsServiceImp userDetailsService;
    private final FriendsSharingService friendsSharingService;

    @Operation(summary = "Register new user")
    @ApiResponse(responseCode = "201", description = "User registered", content = @Content)
    @ApiResponse(responseCode = "400", description = "Wrong authority or user already registered", content = @Content)

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        return userDetailsService.register(registrationRequest);
    }

    @Operation(summary = "Get current user profile, authorization required",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponse(responseCode = "200",
            description = "Current user profile",
            content = @Content(schema = @Schema(implementation = UserDTO.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)

    @GetMapping("/me")
    public UserDTO getProfile(@AuthenticationPrincipal UserAdapter user) {
        return friendsSharingService.getProfile(user.getUser());
    }

    @Operation(summary = "Update current user profile, authorization required",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponse(responseCode = "200",
            description = "Updated user profile",
            content = @Content(schema = @Schema(implementation = UserDTO.class)))
    @ApiResponse(responseCode = "400", description = "Wrong profile data", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)

    @PatchMapping("/me")
    public UserDTO updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                                 @AuthenticationPrincipal UserAdapter user) {
        return friendsSharingService.updateProfile(request, user.getUser());
    }

    @Operation(summary = "Add new book, authorization required",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponse(responseCode = "201",
            description = "Added book",
            content = @Content(
                    schema = @Schema(implementation = BookWithUserDTO.class),
                    examples = @ExampleObject(
                            value = "{\"author\":\"Joshua Bloch\",\"title\":\"Effective Java\"," +
                                    "\"person\":{\"name\":\"vlad\",\"email\":\"email@email.com\"}}")))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)

    @PostMapping("/book/add")
    @ResponseStatus(HttpStatus.CREATED)
    public BookWithUserDTO addBook(@Valid @RequestBody AddBookRequest addBookRequest,
                                   @AuthenticationPrincipal UserAdapter user) {
        return friendsSharingService.addBook(addBookRequest, user.getUser());
    }

    @Operation(summary = "Get book catalog, authorization required",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponse(responseCode = "200",
            description = "Catalog books",
            content = @Content(schema = @Schema(implementation = BookCatalogItems.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)

    @GetMapping("/catalog/books")
    public BookCatalogItems getCatalogBooks(
            @Parameter(description = "Optional title or author search query")
            @RequestParam(required = false) String query) {
        return friendsSharingService.getCatalogBooks(query);
    }

    @Operation(summary = "Get one catalog book, authorization required",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponse(responseCode = "200",
            description = "Catalog book",
            content = @Content(schema = @Schema(implementation = BookCatalogDTO.class)))
    @ApiResponse(responseCode = "400", description = "Catalog book not found", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)

    @GetMapping("/catalog/books/{id}")
    public BookCatalogDTO getCatalogBook(@Parameter(description = "Catalog book ID")
                                         @PathVariable Long id) {
        return friendsSharingService.getCatalogBook(id);
    }

    @Operation(summary = "Add a book from catalog, authorization required",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponse(responseCode = "201",
            description = "Added book",
            content = @Content(schema = @Schema(implementation = BookWithUserDTO.class)))
    @ApiResponse(responseCode = "400", description = "Catalog book not found", content = @Content)
    @ApiResponse(responseCode = "409", description = "Catalog book already added by current user", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)

    @PostMapping("/book/add/from-catalog")
    @ResponseStatus(HttpStatus.CREATED)
    public BookWithUserDTO addBookFromCatalog(@Parameter(description = "Catalog book ID")
                                              @RequestParam Long id,
                                              @AuthenticationPrincipal UserAdapter user) {
        return friendsSharingService.addBookFromCatalog(id, user.getUser());
    }

    /*@Operation(summary = "Add present, authorization required",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponse(responseCode = "200",
            description = "Added present",
            content = @Content(
                    schema = @Schema(implementation = PresentWithUserDTO.class),
                    examples = @ExampleObject(
                            value = "{\"box_color\":\"red\",\"content\":\"books\"," +
                                    "\"person\":{\"name\":\"vlad\",\"email\":\"email@email.com\"}}")))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)

    @PostMapping("/present/add")
    public PresentWithUserDTO addPresent(@Valid @RequestBody AddPresentRequest addPresentRequest,
                                         @AuthenticationPrincipal UserAdapter user) {
        return friendsSharingService.addPresent(addPresentRequest, user.getUser());
    }*/

    //@Operation(summary = "Get a list of the items you hold, authorization required",
    @Operation(summary = "Get a list of the books you hold, authorization required",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponse(responseCode = "200",
            //description = "List of items, where person is the owner of the item",
            description = "List of books, where person is the owner of the book",
            content = @Content(
                    schema = @Schema(implementation = ItemsWithUser.class),
                    examples = @ExampleObject(
                            value = "{\"books\":[{\"author\":\"Joshua Bloch\",\"title\":\"Effective Java\"," +
                                    "\"person\":{\"name\":\"vlad\",\"email\":\"email@email.com\"}}]}")))/*," +
                                    "\"presents\":[{\"box_color\":\"red\",\"content\":\"books\"," +
                                    "\"person:\"{\"name\":\"vlad\",\"email\":\"email@email.com\"}}]}")))*/
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)

    @GetMapping("/held")
    public ItemsWithUser getHeldItems(@AuthenticationPrincipal UserAdapter user) {
        return friendsSharingService.getHeldItems(user.getUser());
    }

    //@Operation(summary = "Get a list of the items you own, authorization required",
    @Operation(summary = "Get a list of the books you own, authorization required",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponse(responseCode = "200",
            //description = "List of items, where person is the holder of the item",
            description = "List of books, where person is the holder of the book",
            content = @Content(
                    schema = @Schema(implementation = ItemsWithUser.class),
                    examples = @ExampleObject(
                            value = "{\"books\":[{\"author\":\"Joshua Bloch\",\"title\":\"Effective Java\"," +
                                    "\"person\":{\"name\":\"vlad\",\"email\":\"email@email.com\"}}]}")))/*," +
                                    "\"presents\":[{\"box_color\":\"red\",\"content\":\"books\"," +
                                    "\"person:\"{\"name\":\"vlad\",\"email\":\"email@email.com\"}}]}")))*/
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)

    @GetMapping("/owned")
    public ItemsWithUser getOwnedItems(@AuthenticationPrincipal UserAdapter user) {
        return friendsSharingService.getOwnedItems(user.getUser());
    }

    @Operation(summary = "Share a book with someone, authorization required",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponse(responseCode = "200",
            description = "Shared book, and info about the user you gave book to",
            content = @Content(
                    schema = @Schema(implementation = BookWithUserDTO.class),
                    examples = @ExampleObject(
                            value = "{\"author\":\"Joshua Bloch\",\"title\":\"Effective Java\"," +
                                    "\"person\":{\"name\":\"vlad\",\"email\":\"email@email.com\"}}")))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "400",
            description = "There is no such user, or you specified a book that you do not own " +
                    "or have already given it to someone", content = @Content)

    @PostMapping("/book/share")
    public BookWithUserDTO shareBook(@Valid @RequestBody GiveBookRequest bookRequest,
                                    @AuthenticationPrincipal UserAdapter user) {
        return friendsSharingService.shareBook(bookRequest, user.getUser());
    }

    @Operation(summary = "Give a book to someone, authorization required",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponse(responseCode = "200",
            description = "Given book, and info about the user you gave book to",
            content = @Content(
                    schema = @Schema(implementation = BookWithUserDTO.class),
                    examples = @ExampleObject(
                            value = "{\"author\":\"Joshua Bloch\",\"title\":\"Effective Java\"," +
                                    "\"person\":{\"name\":\"vlad\",\"email\":\"email@email.com\"}}")))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "400",
            description = "There is no such user, or you specified a book that you do not own " +
                    "or have already given it to someone", content = @Content)

    @PostMapping("/book/give")
    public BookWithUserDTO giveBook(@Valid @RequestBody GiveBookRequest bookRequest,
                                    @AuthenticationPrincipal UserAdapter user) {
        return friendsSharingService.giveBook(bookRequest, user.getUser());
    }

    /*@Operation(summary = "Give a present to someone, authorization required",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponse(responseCode = "200",
            description = "Given present, and info about the user you gave book to",
            content = @Content(
                    schema = @Schema(implementation = PresentWithUserDTO.class),
                    examples = @ExampleObject(
                            value = "{\"box_color\":\"red\",\"content\":\"books\"," +
                                    "\"person\":{\"name\":\"vlad\",\"email\":\"email@email.com\"}}")))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "400",
            description = "There is no such user, or you specified a present that you do not own " +
                    "or have already given it to someone", content = @Content)

    @PostMapping("/present/give")
    public PresentWithUserDTO givePresent(@Valid @RequestBody GivePresentRequest presentRequest,
                                          @AuthenticationPrincipal UserAdapter user) {
        return friendsSharingService.givePresent(presentRequest, user.getUser());
    }*/

    @Operation(summary = "Return a book, authorization required",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponse(responseCode = "200", description = "The book was returned", content = @Content())
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "400",
            description = "You do not hold a book with that title or you are the owner of this book", content = @Content)

    @PostMapping("/book/return")
    public String returnBook(@Valid @RequestBody ReturnBookRequest bookRequest,
                                  @AuthenticationPrincipal UserAdapter user) {
        friendsSharingService.returnBook(bookRequest, user.getUser());
        return "The book was returned";
    }

    //@Operation(summary = "Get all items, ADMIN authority required",
    @Operation(summary = "Get all books, ADMIN authority required",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponse(responseCode = "200",
            //description = "List of items",
            description = "List of books",
            content = @Content(
                    schema = @Schema(implementation = Items.class),
                    examples = @ExampleObject(
                            value = "{\"books\":[{\"id\":1,\"author\":\"Joshua Bloch\"," +
                                    "\"title\":\"Effective Java\",\"holder_id\":1,\"owner_id\":1}]}")))/* +
                                    "\"presents\":[{\"id\":1,\"box_color\":\"red\",\"content\":\"books\"," +
                                    "\"holder_id\":1,\"owner_id\":1}]}")))*/
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Wrong authority", content = @Content)

    @GetMapping("/items")
    public Items getItems() {
        return friendsSharingService.getItems();
    }

    @Operation(summary = "Delete book, ADMIN authority required",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponse(responseCode = "200", description = "Book deleted", content = @Content)
    @ApiResponse(responseCode = "404", description = "Book not found", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Wrong authority", content = @Content)

    @DeleteMapping("/book/delete") //link looks like /book/delete?id=1
    public ResponseEntity<String> deleteBook(@Parameter(description = "Book ID for cancellation")
                                 @RequestParam Long id) {
        if (friendsSharingService.deleteBook(id)) {
            return new ResponseEntity<>("Book deleted", HttpStatus.OK);
        }

        return new ResponseEntity<>("Book not found", HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Force a return of a book, ADMIN authority required",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponse(responseCode = "200", description = "The book was returned", content = @Content())
    @ApiResponse(responseCode = "404", description = "Book not found", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Wrong authority", content = @Content)

    @PostMapping("/book/return/force")
    public ResponseEntity<String> forceReturnBook(@Parameter(description = "Book ID for return")
                                      @RequestParam Long id) {
        if (friendsSharingService.forceReturnBook(id)) {
            return new ResponseEntity<>("The book was returned", HttpStatus.OK);
        }

        return new ResponseEntity<>("Book not found", HttpStatus.NOT_FOUND);
    }

    /*@Operation(summary = "Delete present, ADMIN authority required",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponse(responseCode = "200", description = "Present deleted", content = @Content)
    @ApiResponse(responseCode = "400", description = "Wrong id", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Wrong authority", content = @Content)

    @DeleteMapping("/present/delete") //link looks like /present/delete?id=1
    public ResponseEntity<String> deletePresent(@Parameter(description = "Present ID for cancellation")
                             @RequestParam Long id) {
        if (friendsSharingService.deletePresent(id)) {
            return new ResponseEntity<>("Present deleted", HttpStatus.OK);
        }

        return new ResponseEntity<>("Wrong id", HttpStatus.BAD_REQUEST);
    }*/

    // http://localhost:8080/swagger-ui/index.html to access swagger
}
