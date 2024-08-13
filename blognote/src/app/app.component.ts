import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {PreLoaderComponent} from './pre-loader/pre-loader.component';
import {BirthdayReminderComponent} from './birthday-reminder/birthday-reminder.component';
import {CreatePostComponent} from './create-post/create-post.component';
import {FooterComponent} from './footer/footer.component';
import {FriendSuggestionComponent} from './friend-suggestion/friend-suggestion.component';
import {FriendsListComponent} from './friends-list/friends-list.component';
import {GalleryComponent} from './gallery/gallery.component';
import {HeaderComponent} from './header/header.component';
import {NotificationComponent} from './notification/notification.component';
import {PostComponent} from './post/post.component';
import {SettingsToggleComponent} from './settings-toggle/settings-toggle.component';
import {SidebarComponent} from './sidebar/sidebar.component';
import {StoryComponent} from './story/story.component';
import {UserProfileComponent} from './user-profile/user-profile.component';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet,
    PreLoaderComponent,
    BirthdayReminderComponent,
    CreatePostComponent,
    FooterComponent,
    FriendSuggestionComponent,
    FriendsListComponent,
    GalleryComponent,
    HeaderComponent,
    NotificationComponent,
    PostComponent,
    SettingsToggleComponent,
    SidebarComponent,
    StoryComponent,
    UserProfileComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'blognote';
}
