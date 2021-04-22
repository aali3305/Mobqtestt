package tests;

import common.MyLogger;
import components.CommentComponent;
import components.PostComponent;
import components.UserComponent;
import io.restassured.response.Response;
import models.Comment;
import models.Post;
import models.User;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

public class TC_UserSearchandFindPost {

    private Response allCommentsOnUserPostsResponse;
    private int userId;
    private List<Post> allPostsByUser;
    private List<Comment> allCommentsOnUserPosts;
    private Logger log = MyLogger.log;

    @Test
    public void verifyGetAllUsers() {
        UserComponent.getAllUsers()
                .then()
                .assertThat()
                .body("size()", greaterThan(0));
    }

    @Test
    @Parameters("username") // get the userName from testng.xml
    public void verifyGetUser(String userName) {
        List<User> users = Arrays.asList(UserComponent.getUser(userName).getBody().as(User[].class));
        System.out.println("Hello ");
        if (users.size() > 0) {
            userId = users.get(0).getId();
            System.out.println(userId);
            log.info("userId for the username " + userName + " = " + userId);
        } else
            Assert.fail("No user found with username = " + userName);
    }

    @Test(dependsOnMethods = "verifyGetUser")
    public void verifyGetPostsByUserId() {
        allPostsByUser = Arrays.asList(PostComponent.getPosts(userId).getBody().as(Post[].class));
        allPostsByUser.forEach(post -> Assert.assertEquals(post.getUserId(), userId));
    }

    @Test(dependsOnMethods = "verifyGetPostsByUserId")
    public void verifyPostComments() {
        allPostsByUser.forEach(post -> {
            int postId = post.getId();
            allCommentsOnUserPostsResponse = CommentComponent.getCommentsOnPost(postId);
            allCommentsOnUserPosts = Arrays.asList(allCommentsOnUserPostsResponse.getBody().as(Comment[].class));
            String emailRegex = "^(.+)@(.+)$";
            allCommentsOnUserPosts.forEach(comment -> {
                Assert.assertTrue(comment.getEmail().matches(emailRegex));
            });
        });
    }

    @Test(dependsOnMethods = {"verifyGetUser"})
    @Parameters({"postTitle", "postBody"})
    public void verifyCreatePost(String postTitle, String postBody) {
        PostComponent.createPost(postTitle, postBody, userId)
                .then()
                .assertThat()
                .body("title", equalTo(postTitle))
                .body("body", equalTo(postBody))
                .body("userId", equalTo(userId));
    }

    @Test(dependsOnMethods = {"verifyGetUser"})
    @Parameters({"updatedPostTitle", "fakePostId"})
    public void verifyUpdatePost(String updatedPostTitle, String postId) {
        PostComponent.updatePost("title", updatedPostTitle, postId)
                .then()
                .assertThat()
                .body("title", equalTo(updatedPostTitle));
    }


}

